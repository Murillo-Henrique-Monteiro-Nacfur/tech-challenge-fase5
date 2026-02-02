-- Script para popular dados de predição (Histórico 2022-2026)
-- Cenário: 3 Pontos (Sobra, Falta, Equilíbrio) com Reposição Trimestral
-- Regra: Estoque nunca fica negativo.
-- Atualização: Ponto Falta ajustado em 2026 para sobrar apenas 1 semana de estoque em 31/Mar.

DO $$
DECLARE
    v_client_id BIGINT;
    
    -- IDs dos Pontos
    v_ponto_sobra_id BIGINT;
    v_ponto_falta_id BIGINT;
    v_ponto_equil_id BIGINT;
    
    -- IDs dos Insumos
    v_insumo_dipirona_id BIGINT;
    v_insumo_repelente_id BIGINT;
    
    -- Variáveis de Controle
    v_lote_id BIGINT;
    v_data_cursor DATE;
    v_qtd_demanda INT; 
    v_qtd_real INT;    
    v_estoque_atual INT;
    v_dias_fevereiro INT;
    
    -- Controle de Reposição
    v_qtd_reposicao_sobra INT := 10000; 
    v_qtd_reposicao_falta INT := 2000;  
    v_qtd_reposicao_equil INT := 5000;  
    
    -- Variável auxiliar para reposição dinâmica
    v_qtd_reposicao_atual INT;

    -- Controle de Consumo Médio Diário
    v_media_consumo_sobra INT := 50;    
    v_media_consumo_falta INT := 80;    
    v_media_consumo_equil INT := 55;    

BEGIN
    -- 1. Recuperar ou Criar Client
    SELECT id INTO v_client_id FROM public.clients WHERE client_id = 'farmacia-central-app';
    
    -- 2. Criar Pontos de Dispensação
    INSERT INTO public.ponto_dispensacao (cnes, nome, tipo, email_responsavel, client_id)
    VALUES ('9990001', 'Almoxarifado Central (Sobra)', 'ALMOXARIFADO', 'sobra@sus.gov.br', v_client_id)
    ON CONFLICT DO NOTHING;
    SELECT id INTO v_ponto_sobra_id FROM public.ponto_dispensacao WHERE cnes = '9990001';

    INSERT INTO public.ponto_dispensacao (cnes, nome, tipo, email_responsavel, client_id)
    VALUES ('9990002', 'UBS Centro (Falta)', 'FARMACIA_MUNICIPAL', 'falta@sus.gov.br', v_client_id)
    ON CONFLICT DO NOTHING;
    SELECT id INTO v_ponto_falta_id FROM public.ponto_dispensacao WHERE cnes = '9990002';

    INSERT INTO public.ponto_dispensacao (cnes, nome, tipo, email_responsavel, client_id)
    VALUES ('9990003', 'UBS Bairro (Equilibrio)', 'FARMACIA_MUNICIPAL', 'equil@sus.gov.br', v_client_id)
    ON CONFLICT DO NOTHING;
    SELECT id INTO v_ponto_equil_id FROM public.ponto_dispensacao WHERE cnes = '9990003';

    -- 3. Criar Insumos
    INSERT INTO public.insumo (codigo_catmat, nome_generico, forma_farmaceutica, marca, descricao)
    VALUES ('BR001', 'DIPIRONA SODICA', 'COMPRIMIDO', 'Generico Lab', '500mg cx 10')
    ON CONFLICT DO NOTHING;
    SELECT id INTO v_insumo_dipirona_id FROM public.insumo WHERE codigo_catmat = 'BR001';

    INSERT INTO public.insumo (codigo_catmat, nome_generico, forma_farmaceutica, marca, descricao)
    VALUES ('BR003', 'REPELENTE SPRAY', 'FRASCO', 'XôMosquito', '100ml icaridina')
    ON CONFLICT DO NOTHING;
    SELECT id INTO v_insumo_repelente_id FROM public.insumo WHERE codigo_catmat = 'BR003';

    -- =================================================================================
    -- 4. SIMULAÇÃO DE HISTÓRICO (2022 a Março 2026) - DIPIRONA
    -- =================================================================================
    
    v_data_cursor := '2022-01-01';
    
    WHILE v_data_cursor <= '2026-03-31' LOOP
        
        -- A. REPOSIÇÃO TRIMESTRAL (Jan, Abr, Jul, Out - Dia 1)
        IF (EXTRACT(MONTH FROM v_data_cursor) IN (1, 4, 7, 10) AND EXTRACT(DAY FROM v_data_cursor) = 1) THEN
            
            -- Criar Lote Novo
            INSERT INTO public.lote (numero_lote, insumo_id, data_validade, data_fabricacao, quantidade, id_user)
            VALUES ('LOTE-DIP-' || to_char(v_data_cursor, 'YYYY-MM'), v_insumo_dipirona_id, (v_data_cursor + interval '2 years')::DATE, v_data_cursor, 100000, 1)
            RETURNING id INTO v_lote_id;

            -- Abastecer Pontos e Registrar Histórico de Entrada
            
            -- SOBRA
            IF EXISTS (SELECT 1 FROM public.lote_inventario WHERE id_ponto_dispensacao = v_ponto_sobra_id AND id_lote = v_lote_id) THEN
                UPDATE public.lote_inventario SET quantidade = quantidade + v_qtd_reposicao_sobra WHERE id_ponto_dispensacao = v_ponto_sobra_id AND id_lote = v_lote_id;
            ELSE
                INSERT INTO public.lote_inventario (id_ponto_dispensacao, id_lote, quantidade, data_hora_chegada)
                VALUES (v_ponto_sobra_id, v_lote_id, v_qtd_reposicao_sobra, v_data_cursor + time '08:00:00');
            END IF;
            INSERT INTO public.historico_entrada (id_ponto_dispensacao, id_lote, quantidade, data_hora)
            VALUES (v_ponto_sobra_id, v_lote_id, v_qtd_reposicao_sobra, v_data_cursor + time '08:00:00');


            -- FALTA (Lógica Especial 2026)
            -- Se for Janeiro de 2026, injeta 7800 para durar até o fim de Março e sobrar pouco.
            -- Nos outros anos, injeta 2000 para gerar ruptura rápida.
            IF (EXTRACT(YEAR FROM v_data_cursor) = 2026 AND EXTRACT(MONTH FROM v_data_cursor) = 1) THEN
                v_qtd_reposicao_atual := 7800; -- Suficiente para 97 dias (sobra ~600)
            ELSE
                v_qtd_reposicao_atual := v_qtd_reposicao_falta; -- 2000 (Ruptura)
            END IF;

            IF EXISTS (SELECT 1 FROM public.lote_inventario WHERE id_ponto_dispensacao = v_ponto_falta_id AND id_lote = v_lote_id) THEN
                UPDATE public.lote_inventario SET quantidade = quantidade + v_qtd_reposicao_atual WHERE id_ponto_dispensacao = v_ponto_falta_id AND id_lote = v_lote_id;
            ELSE
                INSERT INTO public.lote_inventario (id_ponto_dispensacao, id_lote, quantidade, data_hora_chegada)
                VALUES (v_ponto_falta_id, v_lote_id, v_qtd_reposicao_atual, v_data_cursor + time '08:00:00');
            END IF;
            INSERT INTO public.historico_entrada (id_ponto_dispensacao, id_lote, quantidade, data_hora)
            VALUES (v_ponto_falta_id, v_lote_id, v_qtd_reposicao_atual, v_data_cursor + time '08:00:00');


            -- EQUILIBRIO
            IF EXISTS (SELECT 1 FROM public.lote_inventario WHERE id_ponto_dispensacao = v_ponto_equil_id AND id_lote = v_lote_id) THEN
                UPDATE public.lote_inventario SET quantidade = quantidade + v_qtd_reposicao_equil WHERE id_ponto_dispensacao = v_ponto_equil_id AND id_lote = v_lote_id;
            ELSE
                INSERT INTO public.lote_inventario (id_ponto_dispensacao, id_lote, quantidade, data_hora_chegada)
                VALUES (v_ponto_equil_id, v_lote_id, v_qtd_reposicao_equil, v_data_cursor + time '08:00:00');
            END IF;
            INSERT INTO public.historico_entrada (id_ponto_dispensacao, id_lote, quantidade, data_hora)
            VALUES (v_ponto_equil_id, v_lote_id, v_qtd_reposicao_equil, v_data_cursor + time '08:00:00');
            
        END IF;

        -- B. CONSUMO DIÁRIO (Com verificação de saldo)
        
        -- --- Ponto SOBRA ---
        v_qtd_demanda := floor(random() * 10 + v_media_consumo_sobra - 5);
        SELECT COALESCE(SUM(quantidade), 0) INTO v_estoque_atual FROM public.lote_inventario WHERE id_ponto_dispensacao = v_ponto_sobra_id AND id_lote = v_lote_id;
        
        IF v_estoque_atual > 0 THEN
            v_qtd_real := LEAST(v_qtd_demanda, v_estoque_atual);
            INSERT INTO public.historico_consumo (id_ponto_dispensacao, id_lote_insumo, quantidade, data_hora, id_user)
            VALUES (v_ponto_sobra_id, v_lote_id, v_qtd_real, v_data_cursor + time '10:00:00', 1);
            UPDATE public.lote_inventario SET quantidade = quantidade - v_qtd_real WHERE id_lote = v_lote_id AND id_ponto_dispensacao = v_ponto_sobra_id;
        END IF;


        -- --- Ponto FALTA ---
        v_qtd_demanda := floor(random() * 10 + v_media_consumo_falta - 5);
        SELECT COALESCE(SUM(quantidade), 0) INTO v_estoque_atual FROM public.lote_inventario WHERE id_ponto_dispensacao = v_ponto_falta_id AND id_lote = v_lote_id;
        
        IF v_estoque_atual > 0 THEN
            v_qtd_real := LEAST(v_qtd_demanda, v_estoque_atual);
            INSERT INTO public.historico_consumo (id_ponto_dispensacao, id_lote_insumo, quantidade, data_hora, id_user)
            VALUES (v_ponto_falta_id, v_lote_id, v_qtd_real, v_data_cursor + time '11:00:00', 1);
            UPDATE public.lote_inventario SET quantidade = quantidade - v_qtd_real WHERE id_lote = v_lote_id AND id_ponto_dispensacao = v_ponto_falta_id;
        END IF;


        -- --- Ponto EQUILIBRIO ---
        v_qtd_demanda := floor(random() * 10 + v_media_consumo_equil - 5);
        SELECT COALESCE(SUM(quantidade), 0) INTO v_estoque_atual FROM public.lote_inventario WHERE id_ponto_dispensacao = v_ponto_equil_id AND id_lote = v_lote_id;
        
        IF v_estoque_atual > 0 THEN
            v_qtd_real := LEAST(v_qtd_demanda, v_estoque_atual);
            INSERT INTO public.historico_consumo (id_ponto_dispensacao, id_lote_insumo, quantidade, data_hora, id_user)
            VALUES (v_ponto_equil_id, v_lote_id, v_qtd_real, v_data_cursor + time '12:00:00', 1);
            UPDATE public.lote_inventario SET quantidade = quantidade - v_qtd_real WHERE id_lote = v_lote_id AND id_ponto_dispensacao = v_ponto_equil_id;
        END IF;

        -- Avança dia
        v_data_cursor := v_data_cursor + 1;
    END LOOP;

    -- =================================================================================
    -- REPELENTE (Apenas 2026 - Tendência de Alta) - Focado no Ponto EQUILIBRIO
    -- =================================================================================
    INSERT INTO public.lote (numero_lote, insumo_id, data_validade, data_fabricacao, quantidade, id_user)
    VALUES ('LOTE-REP-2026', v_insumo_repelente_id, '2028-01-01', '2026-01-01', 10000, 1) RETURNING id INTO v_lote_id;
    
    INSERT INTO public.lote_inventario (id_ponto_dispensacao, id_lote, quantidade, data_hora_chegada)
    VALUES (v_ponto_equil_id, v_lote_id, 5000, '2026-01-01 08:00:00');
    
    -- Registro Entrada Repelente
    INSERT INTO public.historico_entrada (id_ponto_dispensacao, id_lote, quantidade, data_hora)
    VALUES (v_ponto_equil_id, v_lote_id, 5000, '2026-01-01 08:00:00');

    v_data_cursor := '2026-01-01';
    v_dias_fevereiro := 0;
    
    WHILE v_data_cursor <= '2026-03-31' LOOP
         IF EXTRACT(MONTH FROM v_data_cursor) = 1 THEN
             v_qtd_demanda := 25; 
         ELSIF EXTRACT(MONTH FROM v_data_cursor) = 2 THEN
             v_dias_fevereiro := v_dias_fevereiro + 1;
             v_qtd_demanda := 25 + floor(v_dias_fevereiro * 4.5); 
         ELSE
             v_qtd_demanda := 150; 
         END IF;

         -- Verifica Estoque Repelente
         SELECT COALESCE(SUM(quantidade), 0) INTO v_estoque_atual FROM public.lote_inventario WHERE id_ponto_dispensacao = v_ponto_equil_id AND id_lote = v_lote_id;

         IF v_estoque_atual > 0 THEN
             v_qtd_real := LEAST(v_qtd_demanda, v_estoque_atual);
             
             INSERT INTO public.historico_consumo (id_ponto_dispensacao, id_lote_insumo, quantidade, data_hora, id_user)
             VALUES (v_ponto_equil_id, v_lote_id, v_qtd_real, v_data_cursor + time '17:00:00', 1);
             
             UPDATE public.lote_inventario SET quantidade = quantidade - v_qtd_real WHERE id_lote = v_lote_id AND id_ponto_dispensacao = v_ponto_equil_id;
         END IF;
         
         v_data_cursor := v_data_cursor + 1;
    END LOOP;

END $$;
