-- ============================================================================
-- SCRIPT 03: CENÁRIO DE AVALIAÇÃO - FEVEREIRO 2026
-- Focado em validar: Previsão Diária, Sazonal, Transferência e Validade
-- Data de Referência para Teste: 25/02/2026
-- ============================================================================

-- 1. DADOS MESTRES ESPECÍFICOS
-- ============================================================================

-- Pontos de Dispensação de Teste
-- AJUSTADO PARA RESPEITAR O CHECK CONSTRAINT (FARMACIA_CENTRAL, FARMACIA_MUNICIPAL, ALMOXARIFADO)
INSERT INTO public.ponto_dispensacao (cnes, nome, tipo, email_responsavel, client_id)
VALUES 
('8880001', 'Hospital Avaliação (Crítico)', 'FARMACIA_MUNICIPAL', 'critico@avaliacao.com', (SELECT id FROM public.clients WHERE client_id = 'farmacia-central-app')),
('8880002', 'UBS Avaliação (Doador)', 'ALMOXARIFADO', 'doador@avaliacao.com', (SELECT id FROM public.clients WHERE client_id = 'farmacia-central-app')),
('8880003', 'Farmácia Avaliação (Desperdício)', 'FARMACIA_MUNICIPAL', 'desperdicio@avaliacao.com', (SELECT id FROM public.clients WHERE client_id = 'farmacia-central-app')),
('8880004', 'Posto Avaliação (Justo)', 'FARMACIA_MUNICIPAL', 'justo@avaliacao.com', (SELECT id FROM public.clients WHERE client_id = 'farmacia-central-app'))
ON CONFLICT (cnes) DO NOTHING;

-- Insumos de Teste
INSERT INTO public.insumo (codigo_catmat, nome_generico, forma_farmaceutica, marca, descricao)
VALUES 
('TEST001', 'DIPIRONA TESTE (DIARIO)', 'COMPRIMIDO', 'LabTest', 'Teste Diário'),
('TEST002', 'ANTIALERGICO TESTE (SAZONAL)', 'XAROPE', 'SazonalLab', 'Teste Sazonal'),
('TEST003', 'VITAMINA C TESTE (VALIDADE)', 'EFERVESCENTE', 'VitaTest', 'Teste Validade')
ON CONFLICT (codigo_catmat) DO NOTHING;


-- ============================================================================
-- CENÁRIO A: PREVISÃO DIÁRIA + TRANSFERÊNCIA (DIPIRONA - TEST001)
-- Ponto Crítico: Estoque 50, Consumo 20/dia -> Acaba em 2.5 dias (CRÍTICO)
-- Ponto Doador: Estoque 2000, Consumo 10/dia -> Sobra Segura 1700 (DOADOR)
-- Ponto Justo: Estoque 300, Consumo 10/dia -> Sobra 0 (NÃO DOADOR)
-- ============================================================================

-- A.1 Lotes
INSERT INTO public.lote (numero_lote, insumo_id, data_validade, data_fabricacao, quantidade, id_user) VALUES 
('LOTE-DIP-CRIT', (SELECT id FROM public.insumo WHERE codigo_catmat = 'TEST001'), '2027-01-01', '2025-01-01', 1000, 1),
('LOTE-DIP-DOAD', (SELECT id FROM public.insumo WHERE codigo_catmat = 'TEST001'), '2027-01-01', '2025-01-01', 5000, 1),
('LOTE-DIP-JUST', (SELECT id FROM public.insumo WHERE codigo_catmat = 'TEST001'), '2027-01-01', '2025-01-01', 1000, 1);

-- A.2 Entradas Iniciais (Jan/2026)
-- REMOVIDO id_user POIS A TABELA historico_entrada NÃO POSSUI ESSA COLUNA
INSERT INTO public.historico_entrada (id_ponto_dispensacao, id_lote, quantidade, data_hora) VALUES 
((SELECT id FROM public.ponto_dispensacao WHERE cnes = '8880001'), (SELECT id FROM public.lote WHERE numero_lote = 'LOTE-DIP-CRIT'), 650, '2026-01-01 08:00:00'),
((SELECT id FROM public.ponto_dispensacao WHERE cnes = '8880002'), (SELECT id FROM public.lote WHERE numero_lote = 'LOTE-DIP-DOAD'), 2500, '2026-01-01 08:00:00'),
((SELECT id FROM public.ponto_dispensacao WHERE cnes = '8880004'), (SELECT id FROM public.lote WHERE numero_lote = 'LOTE-DIP-JUST'), 600, '2026-01-01 08:00:00');

-- A.3 Consumo Recente (Jan e Fev 2026)
-- Crítico (20/dia)
INSERT INTO public.historico_consumo (id_ponto_dispensacao, id_lote_insumo, quantidade, data_hora, id_user)
SELECT (SELECT id FROM public.ponto_dispensacao WHERE cnes = '8880001'), (SELECT id FROM public.lote WHERE numero_lote = 'LOTE-DIP-CRIT'), 20, d + time '10:00:00', 1
FROM generate_series('2026-01-25'::timestamp, '2026-02-24'::timestamp, '1 day') AS d;

-- Doador (10/dia)
INSERT INTO public.historico_consumo (id_ponto_dispensacao, id_lote_insumo, quantidade, data_hora, id_user)
SELECT (SELECT id FROM public.ponto_dispensacao WHERE cnes = '8880002'), (SELECT id FROM public.lote WHERE numero_lote = 'LOTE-DIP-DOAD'), 10, d + time '10:00:00', 1
FROM generate_series('2026-01-25'::timestamp, '2026-02-24'::timestamp, '1 day') AS d;

-- Justo (10/dia)
INSERT INTO public.historico_consumo (id_ponto_dispensacao, id_lote_insumo, quantidade, data_hora, id_user)
SELECT (SELECT id FROM public.ponto_dispensacao WHERE cnes = '8880004'), (SELECT id FROM public.lote WHERE numero_lote = 'LOTE-DIP-JUST'), 10, d + time '10:00:00', 1
FROM generate_series('2026-01-25'::timestamp, '2026-02-24'::timestamp, '1 day') AS d;


-- ============================================================================
-- CENÁRIO B: PREVISÃO SAZONAL (ANTIALÉRGICO - TEST002)
-- Ponto Crítico: Estoque 100. Histórico Março: 50/dia -> Acaba em 2 dias.
-- Ponto Doador: Estoque 2000. Histórico Março: 2/dia -> Sobra Segura.
-- Ponto Justo: Estoque 300. Histórico Março: 10/dia -> Sobra 0.
-- ============================================================================

-- B.1 Lote (Compartilhado para simplificar, mas com entradas separadas)
INSERT INTO public.lote (numero_lote, insumo_id, data_validade, data_fabricacao, quantidade, id_user)
VALUES ('LOTE-SAZ-001', (SELECT id FROM public.insumo WHERE codigo_catmat = 'TEST002'), '2028-01-01', '2025-01-01', 10000, 1);

-- Lote Exclusivo para Histórico (Para não consumir o saldo do lote atual)
INSERT INTO public.lote (numero_lote, insumo_id, data_validade, data_fabricacao, quantidade, id_user)
VALUES ('LOTE-SAZ-HIST', (SELECT id FROM public.insumo WHERE codigo_catmat = 'TEST002'), '2025-01-01', '2020-01-01', 100000, 1);

-- B.2 Entradas (Estoque Atual em 25/02/2026)
-- REMOVIDO id_user
INSERT INTO public.historico_entrada (id_ponto_dispensacao, id_lote, quantidade, data_hora) VALUES 
((SELECT id FROM public.ponto_dispensacao WHERE cnes = '8880001'), (SELECT id FROM public.lote WHERE numero_lote = 'LOTE-SAZ-001'), 130, '2026-01-01 08:00:00'), -- Crítico (sobra 100 após consumo recente)
((SELECT id FROM public.ponto_dispensacao WHERE cnes = '8880002'), (SELECT id FROM public.lote WHERE numero_lote = 'LOTE-SAZ-001'), 2060, '2026-01-01 08:00:00'), -- Doador (sobra 2000)
((SELECT id FROM public.ponto_dispensacao WHERE cnes = '8880004'), (SELECT id FROM public.lote WHERE numero_lote = 'LOTE-SAZ-001'), 330, '2026-01-01 08:00:00'); -- Justo (sobra 300)

-- B.3 Consumo Recente (Jan/Fev 2026) - Baixo para todos (Engana a previsão diária)
INSERT INTO public.historico_consumo (id_ponto_dispensacao, id_lote_insumo, quantidade, data_hora, id_user)
SELECT (SELECT id FROM public.ponto_dispensacao WHERE cnes = '8880001'), (SELECT id FROM public.lote WHERE numero_lote = 'LOTE-SAZ-001'), 1, d + time '12:00:00', 1
FROM generate_series('2026-01-25'::timestamp, '2026-02-24'::timestamp, '1 day') AS d;

INSERT INTO public.historico_consumo (id_ponto_dispensacao, id_lote_insumo, quantidade, data_hora, id_user)
SELECT (SELECT id FROM public.ponto_dispensacao WHERE cnes = '8880002'), (SELECT id FROM public.lote WHERE numero_lote = 'LOTE-SAZ-001'), 2, d + time '12:00:00', 1
FROM generate_series('2026-01-25'::timestamp, '2026-02-24'::timestamp, '1 day') AS d;

INSERT INTO public.historico_consumo (id_ponto_dispensacao, id_lote_insumo, quantidade, data_hora, id_user)
SELECT (SELECT id FROM public.ponto_dispensacao WHERE cnes = '8880004'), (SELECT id FROM public.lote WHERE numero_lote = 'LOTE-SAZ-001'), 1, d + time '12:00:00', 1
FROM generate_series('2026-01-25'::timestamp, '2026-02-24'::timestamp, '1 day') AS d;

-- B.4 Histórico Sazonal (Março de 2022, 2023, 2024, 2025)
-- Crítico: Alto (50/dia)
INSERT INTO public.historico_consumo (id_ponto_dispensacao, id_lote_insumo, quantidade, data_hora, id_user)
SELECT (SELECT id FROM public.ponto_dispensacao WHERE cnes = '8880001'), (SELECT id FROM public.lote WHERE numero_lote = 'LOTE-SAZ-HIST'), 50, d + time '14:00:00', 1
FROM generate_series('2022-03-01'::timestamp, '2025-03-31'::timestamp, '1 day') AS d WHERE extract(month from d) = 3;

-- Doador: Baixo (2/dia) -> Garante que ele é um doador seguro
INSERT INTO public.historico_consumo (id_ponto_dispensacao, id_lote_insumo, quantidade, data_hora, id_user)
SELECT (SELECT id FROM public.ponto_dispensacao WHERE cnes = '8880002'), (SELECT id FROM public.lote WHERE numero_lote = 'LOTE-SAZ-HIST'), 2, d + time '14:00:00', 1
FROM generate_series('2022-03-01'::timestamp, '2025-03-31'::timestamp, '1 day') AS d WHERE extract(month from d) = 3;

-- Justo: Médio (10/dia) -> Garante que ele consome o que tem (300 estoque / 10 dia = 30 dias). Excedente = 0.
INSERT INTO public.historico_consumo (id_ponto_dispensacao, id_lote_insumo, quantidade, data_hora, id_user)
SELECT (SELECT id FROM public.ponto_dispensacao WHERE cnes = '8880004'), (SELECT id FROM public.lote WHERE numero_lote = 'LOTE-SAZ-HIST'), 10, d + time '14:00:00', 1
FROM generate_series('2022-03-01'::timestamp, '2025-03-31'::timestamp, '1 day') AS d WHERE extract(month from d) = 3;


-- ============================================================================
-- CENÁRIO C: ALERTA DE VALIDADE (VITAMINA C - TEST003)
-- ============================================================================
-- C.1 Lote Vencendo
INSERT INTO public.lote (numero_lote, insumo_id, data_validade, data_fabricacao, quantidade, id_user)
VALUES ('LOTE-VENC-001', (SELECT id FROM public.insumo WHERE codigo_catmat = 'TEST003'), '2026-04-10', '2024-04-10', 1000, 1);

-- C.2 Entrada
-- REMOVIDO id_user
INSERT INTO public.historico_entrada (id_ponto_dispensacao, id_lote, quantidade, data_hora)
VALUES ((SELECT id FROM public.ponto_dispensacao WHERE cnes = '8880003'), (SELECT id FROM public.lote WHERE numero_lote = 'LOTE-VENC-001'), 560, '2026-01-01 08:00:00');

-- C.3 Consumo Recente (Baixo)
INSERT INTO public.historico_consumo (id_ponto_dispensacao, id_lote_insumo, quantidade, data_hora, id_user)
SELECT (SELECT id FROM public.ponto_dispensacao WHERE cnes = '8880003'), (SELECT id FROM public.lote WHERE numero_lote = 'LOTE-VENC-001'), 2, d + time '15:00:00', 1
FROM generate_series('2026-01-25'::timestamp, '2026-02-24'::timestamp, '1 day') AS d;


-- ============================================================================
-- 7. ATUALIZAÇÃO DO INVENTÁRIO (LOTE_INVENTARIO)
-- ============================================================================
DELETE FROM public.lote_inventario 
WHERE id_ponto_dispensacao IN (
    SELECT id FROM public.ponto_dispensacao WHERE cnes IN ('8880001', '8880002', '8880003', '8880004')
);

INSERT INTO public.lote_inventario (id_ponto_dispensacao, id_lote, quantidade, data_hora_chegada)
SELECT 
    e.id_ponto_dispensacao,
    e.id_lote,
    (SUM(e.quantidade) - COALESCE((
        SELECT SUM(c.quantidade) 
        FROM public.historico_consumo c 
        WHERE c.id_ponto_dispensacao = e.id_ponto_dispensacao AND c.id_lote_insumo = e.id_lote
    ), 0)) AS saldo_final,
    MAX(e.data_hora)
FROM public.historico_entrada e
WHERE e.id_ponto_dispensacao IN (SELECT id FROM public.ponto_dispensacao WHERE cnes IN ('8880001', '8880002', '8880003', '8880004'))
GROUP BY e.id_ponto_dispensacao, e.id_lote
HAVING (SUM(e.quantidade) - COALESCE((
        SELECT SUM(c.quantidade) 
        FROM public.historico_consumo c 
        WHERE c.id_ponto_dispensacao = e.id_ponto_dispensacao AND c.id_lote_insumo = e.id_lote
    ), 0)) > 0;
