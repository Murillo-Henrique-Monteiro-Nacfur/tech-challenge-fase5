# TODO - Registro de Consumo de Medicamentos

## 1. DTOs
- [ ] Criar `ItemConsumoDTO` (sem id_transacao_externa).
- [ ] Criar `RegistroConsumoDTO`.

## 2. Validações
- [ ] Criar interface `ConsumoValidation`.
- [ ] Implementar `EstoqueSuficienteValidator`.
- [ ] Implementar `PontoPertenceAoClienteValidator` (Segurança).

## 3. Use Cases
- [ ] **RegistrarConsumoUseCase**:
    - `@Transactional`.
    - Orquestrar validações.
    - Atualizar `LoteInventario` (subtração).
    - Persistir `HistoricoConsumo`.

## 4. Controller
- [ ] **ConsumoController**:
    - Endpoint `POST /api/v1/consumo`.
    - Extração de Token JWT.
