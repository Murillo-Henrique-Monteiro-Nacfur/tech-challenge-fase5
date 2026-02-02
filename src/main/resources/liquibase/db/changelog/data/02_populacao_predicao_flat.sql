-- ============================================================================
-- SCRIPT 02: POPULAÇÃO DE DADOS PARA PREDIÇÃO (VERSÃO FLAT / SEM LOOPS)
-- Cenário: 3 Pontos (Sobra, Falta, Equilíbrio)
-- Período: 2022 a Março 2026
-- ============================================================================

-- 1. LIMPEZA PRÉVIA (Opcional, para garantir estado limpo se rodar manualmente)
-- DELETE FROM public.movimentacoes;
-- DELETE FROM public.historico_consumo;
-- DELETE FROM public.historico_entrada;
-- DELETE FROM public.lote_inventario;
-- DELETE FROM public.lote;
-- DELETE FROM public.insumo WHERE codigo_catmat IN ('BR001', 'BR002', 'BR003');
-- DELETE FROM public.ponto_dispensacao WHERE cnes IN ('9990001', '9990002', '9990003');

-- ============================================================================
-- 2. DADOS MESTRES (CLIENT, PONTOS, INSUMOS)
-- ============================================================================

-- Garantir Client
INSERT INTO public.clients (client_id, client_secret, name, scopes)
VALUES ('farmacia-central-app', '$2a$10$KkUrgvp0fZ8VURE6zz3gRur47uwgdCeirCdOA0xvt8oS2rPJmFXom', 'Sistema Farmácia Central', 'read write')
ON CONFLICT (client_id) DO NOTHING;

-- Pontos de Dispensação
INSERT INTO public.ponto_dispensacao (cnes, nome, tipo, email_responsavel, client_id)
VALUES 
('9990001', 'Almoxarifado Central (Sobra)', 'ALMOXARIFADO', 'sobra@sus.gov.br', (SELECT id FROM public.clients WHERE client_id = 'farmacia-central-app')),
('9990002', 'UBS Centro (Falta)', 'FARMACIA_MUNICIPAL', 'falta@sus.gov.br', (SELECT id FROM public.clients WHERE client_id = 'farmacia-central-app')),
('9990003', 'UBS Bairro (Equilibrio)', 'FARMACIA_MUNICIPAL', 'equil@sus.gov.br', (SELECT id FROM public.clients WHERE client_id = 'farmacia-central-app'))
ON CONFLICT DO NOTHING;

-- Insumos
INSERT INTO public.insumo (codigo_catmat, nome_generico, forma_farmaceutica, marca, descricao)
VALUES 
('BR001', 'DIPIRONA SODICA', 'COMPRIMIDO', 'Generico Lab', '500mg cx 10'),
('BR002', 'ACEBROFILINA', 'XAROPE', 'RespiraBem', 'Adulto 120ml'),
('BR003', 'REPELENTE SPRAY', 'FRASCO', 'XôMosquito', '100ml icaridina')
ON CONFLICT DO NOTHING;

-- ============================================================================
-- 3. GERAÇÃO DE LOTES E ENTRADAS (REPOSIÇÃO TRIMESTRAL) - 2022 a 2026
-- ============================================================================

-- 3.1 Criar Lotes (Um por trimestre para cada insumo)
-- Gera datas: 2022-01-01, 2022-04-01, ..., 2026-01-01
INSERT INTO public.lote (numero_lote, insumo_id, data_validade, data_fabricacao, quantidade, id_user)
SELECT 
    'LOTE-DIP-' || to_char(d, 'YYYY-MM'),
    (SELECT id FROM public.insumo WHERE codigo_catmat = 'BR001'),
    (d + interval '2 years')::DATE,
    d::DATE,
    100000,
    1
FROM generate_series('2022-01-01'::date, '2026-01-01'::date, '3 months') AS d;

-- 3.2 Registrar Entradas (Historico Entrada)
-- Vincula os lotes criados acima aos 3 pontos com as quantidades definidas

-- Ponto SOBRA (Recebe 10.000)
INSERT INTO public.historico_entrada (id_ponto_dispensacao, id_lote, quantidade, data_hora, id_user)
SELECT 
    (SELECT id FROM public.ponto_dispensacao WHERE cnes = '9990001'),
    l.id,
    10000,
    l.data_fabricacao + time '08:00:00',
    1
FROM public.lote l
WHERE l.numero_lote LIKE 'LOTE-DIP-%';

-- Ponto FALTA (Recebe 2.000)
INSERT INTO public.historico_entrada (id_ponto_dispensacao, id_lote, quantidade, data_hora, id_user)
SELECT 
    (SELECT id FROM public.ponto_dispensacao WHERE cnes = '9990002'),
    l.id,
    2000,
    l.data_fabricacao + time '08:00:00',
    1
FROM public.lote l
WHERE l.numero_lote LIKE 'LOTE-DIP-%';

-- Ponto EQUILIBRIO (Recebe 5.000)
INSERT INTO public.historico_entrada (id_ponto_dispensacao, id_lote, quantidade, data_hora, id_user)
SELECT 
    (SELECT id FROM public.ponto_dispensacao WHERE cnes = '9990003'),
    l.id,
    5000,
    l.data_fabricacao + time '08:00:00',
    1
FROM public.lote l
WHERE l.numero_lote LIKE 'LOTE-DIP-%';


-- ============================================================================
-- 4. GERAÇÃO DE CONSUMO (SAÍDAS) - DIPIRONA
-- ============================================================================

-- 4.1 Ponto SOBRA (Consumo ~50/dia, todos os dias)
INSERT INTO public.historico_consumo (id_ponto_dispensacao, id_lote_insumo, quantidade, data_hora, id_user)
SELECT 
    (SELECT id FROM public.ponto_dispensacao WHERE cnes = '9990001'),
    l.id,
    floor(random() * 10 + 45)::int, -- Média 50
    d + time '10:00:00',
    1
FROM generate_series('2022-01-01'::timestamp, '2026-03-31'::timestamp, '1 day') AS d
JOIN public.lote l ON l.numero_lote = 'LOTE-DIP-' || to_char(date_trunc('quarter', d), 'YYYY-MM')
WHERE l.insumo_id = (SELECT id FROM public.insumo WHERE codigo_catmat = 'BR001');

-- 4.2 Ponto FALTA (Consumo ~80/dia, MAS APENAS NOS PRIMEIROS 25 DIAS DO TRIMESTRE)
-- Isso simula a ruptura de estoque sem precisar de lógica procedural complexa.
INSERT INTO public.historico_consumo (id_ponto_dispensacao, id_lote_insumo, quantidade, data_hora, id_user)
SELECT 
    (SELECT id FROM public.ponto_dispensacao WHERE cnes = '9990002'),
    l.id,
    floor(random() * 10 + 75)::int, -- Média 80
    d + time '11:00:00',
    1
FROM generate_series('2022-01-01'::timestamp, '2026-03-31'::timestamp, '1 day') AS d
JOIN public.lote l ON l.numero_lote = 'LOTE-DIP-' || to_char(date_trunc('quarter', d), 'YYYY-MM')
WHERE 
    l.insumo_id = (SELECT id FROM public.insumo WHERE codigo_catmat = 'BR001')
    AND (d::date - date_trunc('quarter', d)::date) < 25; -- LIMITADOR: Só consome nos primeiros 25 dias

-- 4.3 Ponto EQUILIBRIO (Consumo ~55/dia, todos os dias)
INSERT INTO public.historico_consumo (id_ponto_dispensacao, id_lote_insumo, quantidade, data_hora, id_user)
SELECT 
    (SELECT id FROM public.ponto_dispensacao WHERE cnes = '9990003'),
    l.id,
    floor(random() * 10 + 50)::int, -- Média 55
    d + time '12:00:00',
    1
FROM generate_series('2022-01-01'::timestamp, '2026-03-31'::timestamp, '1 day') AS d
JOIN public.lote l ON l.numero_lote = 'LOTE-DIP-' || to_char(date_trunc('quarter', d), 'YYYY-MM')
WHERE l.insumo_id = (SELECT id FROM public.insumo WHERE codigo_catmat = 'BR001');


-- ============================================================================
-- 5. CASO ESPECIAL: REPELENTE (2026 - TENDÊNCIA DE ALTA)
-- ============================================================================

-- 5.1 Criar Lote Único Repelente 2026
INSERT INTO public.lote (numero_lote, insumo_id, data_validade, data_fabricacao, quantidade, id_user)
VALUES (
    'LOTE-REP-2026', 
    (SELECT id FROM public.insumo WHERE codigo_catmat = 'BR003'), 
    '2028-01-01', 
    '2026-01-01', 
    10000, 
    1
);

-- 5.2 Entrada Repelente (Ponto Equilíbrio)
INSERT INTO public.historico_entrada (id_ponto_dispensacao, id_lote, quantidade, data_hora, id_user)
VALUES (
    (SELECT id FROM public.ponto_dispensacao WHERE cnes = '9990003'),
    (SELECT id FROM public.lote WHERE numero_lote = 'LOTE-REP-2026'),
    5000,
    '2026-01-01 08:00:00',
    1
);

-- 5.3 Consumo Repelente (Lógica de Ramp-up)
INSERT INTO public.historico_consumo (id_ponto_dispensacao, id_lote_insumo, quantidade, data_hora, id_user)
SELECT 
    (SELECT id FROM public.ponto_dispensacao WHERE cnes = '9990003'),
    (SELECT id FROM public.lote WHERE numero_lote = 'LOTE-REP-2026'),
    CASE 
        WHEN extract(month from d) = 1 THEN 25 -- Janeiro: 25 fixo
        WHEN extract(month from d) = 2 THEN (25 + (extract(day from d) * 4.5))::int -- Fev: Sobe de 25 a 150
        ELSE 150 -- Março: 150 fixo
    END,
    d + time '17:00:00',
    1
FROM generate_series('2026-01-01'::timestamp, '2026-03-31'::timestamp, '1 day') AS d;


-- ============================================================================
-- 6. CONSOLIDAÇÃO DO ESTOQUE FINAL (LOTE_INVENTARIO)
-- ============================================================================
-- Como usamos inserções diretas, o lote_inventario precisa ser calculado
-- baseado no (Total Entradas - Total Saídas).

-- Limpa inventário atual para recalcular
DELETE FROM public.lote_inventario 
WHERE id_ponto_dispensacao IN (
    SELECT id FROM public.ponto_dispensacao WHERE cnes IN ('9990001', '9990002', '9990003')
);

-- Insere saldo calculado
INSERT INTO public.lote_inventario (id_ponto_dispensacao, id_lote, quantidade, data_hora_chegada)
SELECT 
    e.id_ponto_dispensacao,
    e.id_lote,
    (SUM(e.quantidade) - COALESCE((
        SELECT SUM(c.quantidade) 
        FROM public.historico_consumo c 
        WHERE c.id_ponto_dispensacao = e.id_ponto_dispensacao AND c.id_lote_insumo = e.id_lote
    ), 0)) AS saldo_final,
    MAX(e.data_hora) -- Data da última entrada
FROM public.historico_entrada e
GROUP BY e.id_ponto_dispensacao, e.id_lote
HAVING (SUM(e.quantidade) - COALESCE((
        SELECT SUM(c.quantidade) 
        FROM public.historico_consumo c 
        WHERE c.id_ponto_dispensacao = e.id_ponto_dispensacao AND c.id_lote_insumo = e.id_lote
    ), 0)) > 0; -- Só insere se tiver saldo positivo
