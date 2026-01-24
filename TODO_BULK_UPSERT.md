# TODO - Implementação de Carga de Estoque (Bulk Upsert)

## 1. Banco de Dados e Entidades
- [ ] **Liquibase**: Adicionar coluna `client_id` na tabela `ponto_dispensacao`.
- [ ] **Entidade**: Atualizar `PontoDispensacao` com o relacionamento/campo `clientId`.
- [ ] **Repositório**: Criar método de busca segura `findByClientId`.

## 2. DTOs (JSON Structure)
- [ ] Criar `InsumoDetalheDTO`.
- [ ] Criar `ItemCargaDTO`.
- [ ] Criar `CargaEstoqueDTO`.

## 3. Use Cases (Regras de Negócio)
- [ ] **InsumoUpsertUseCase**: Garantir cadastro de insumos.
- [ ] **LoteUpsertUseCase**: Garantir cadastro de lotes mestres.
- [ ] **EstoqueMovimentacaoUseCase**: Atualizar saldo em `lote_inventario`.
- [ ] **ProcessarCargaEstoqueUseCase**:
    - Anotação `@Transactional`.
    - Validação de Segurança (Ponto pertence ao Cliente?).
    - Orquestração dos passos acima.

## 4. Controller
- [ ] **EstoqueController**: Endpoint `POST /estoque/carga`.
