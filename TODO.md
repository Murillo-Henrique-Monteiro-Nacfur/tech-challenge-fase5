# TODO List - Endpoints e Funcionalidades

## 1. Insumos (Insumo)
- [ ] **POST /insumos**: Cadastrar novos insumos (medicamentos, materiais, etc.).
    - *Payload*: `codigo_catmat`, `nome_generico`, `forma_farmaceutica`, `marca`, `descricao`.

## 2. Lotes (Lote)
- [ ] **POST /lotes**: Informar novos lotes de insumos.
    - *Payload*: `numero_lote`, `insumo_id`, `data_validade`, `data_fabricacao`, `quantidade`, `id_user`.

## 3. Inventário (LoteInventario)
- [ ] **POST /inventario/entrada**: Adicionar itens ao lote de inventário de um ponto de dispensação.
    - *Payload*: `ponto_dispensacao_id`, `id_lote`, `quantidade`.
- [ ] **GET /inventario/{ponto_dispensacao_id}**: Consultar o inventário atual de um ponto de dispensação.

## 4. Histórico de Consumo (HistoricoConsumo)
- [ ] **POST /consumo**: Receber dados de consumo de medicamentos dos locais de distribuição.
    - *Payload*: `ponto_dispensacao_id`, `id_lote_insumo`, `quantidade`, `data_hora`, `id_user`.
    - *Nota*: O cliente enviará uma lista de dados.

## 5. Movimentações (Movimentacoes)
- [ ] **POST /movimentacoes**: Registrar transferência de insumos entre pontos de dispensação.
    - *Payload*: `ponto_dispensacao_origem_id`, `ponto_dispensacao_destino_id`, `id_lote`, `quantidade`, `id_user`.

## 6. Pontos de Dispensação (PontoDispensacao)
- [ ] **POST /pontos-dispensacao**: Cadastrar novos pontos (Farmácias, Almoxarifados).
- [ ] **GET /pontos-dispensacao**: Listar pontos de dispensação.
