# TODO - Implementação de Carga de Estoque (Bulk Upsert)

## 1. Banco de Dados e Entidades
- [ ] **Liquibase**: Adicionar coluna `client_id` na tabela `ponto_dispensacao`.
- [ ] **Entidade PontoDispensacao**: Criar classe mapeando a tabela `ponto_dispensacao` (incluindo `clientId`).
- [ ] **Entidade Lote**: Criar classe mapeando a tabela `lote`.
- [ ] **Entidade LoteInventario**: Criar classe mapeando a tabela `lote_inventario`.
- [ ] **Entidade HistoricoConsumo**: Criar classe mapeando a tabela `historico_consumo`.
- [ ] **Entidade Movimentacoes**: Criar classe mapeando a tabela `movimentacoes`.

## 2. Repositórios
- [ ] **PontoDispensacaoRepository**: Com método `findByClientId`.
- [ ] **LoteRepository**: Com método `findByNumeroLoteAndInsumoId`.
- [ ] **LoteInventarioRepository**: Com método `findByPontoDispensacaoIdAndLoteId`.
- [ ] **HistoricoConsumoRepository**: CRUD básico.
- [ ] **MovimentacoesRepository**: CRUD básico.

## 3. DTOs (JSON Structure)
- [ ] Criar `InsumoDetalheDTO`.
- [ ] Criar `ItemCargaDTO`.
- [ ] Criar `CargaEstoqueDTO`.

## 4. Use Cases (Regras de Negócio)
- [ ] **InsumoUpsertUseCase**: Garantir cadastro de insumos.
- [ ] **LoteUpsertUseCase**: Garantir cadastro de lotes mestres.
- [ ] **EstoqueMovimentacaoUseCase**: Atualizar saldo em `lote_inventario`.
- [ ] **ProcessarCargaEstoqueUseCase**:
    - Anotação `@Transactional`.
    - Validação de Segurança (Ponto pertence ao Cliente?).
    - Orquestração dos passos acima.

## 5. Controller
- [ ] **EstoqueController**: Endpoint `POST /estoque/carga`.
