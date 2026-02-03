# Guia de Testes - Fluxo Completo

Este guia mostra como testar o fluxo completo, desde a autenticação até o registro de consumo.

**Pré-requisitos:**
- A aplicação Java deve estar rodando.
- `curl` e `jq` instalados no seu terminal.
- O banco de dados deve ter sido iniciado com a carga inicial do Liquibase.

---

### Passo 1: Autenticação (Obter Token)

Primeiro, vamos obter um token de acesso para o sistema da "Farmácia Central".

```bash
# Comando para obter o token
TOKEN=$(curl -s -X POST http://localhost:8080/oauth2/token \\
  -H "Content-Type: application/json" \\
  -d '{
    "clientId": "farmacia-central-app",
    "clientSecret": "secret",
    "grantType": "client_credentials"
  }' | jq -r .access_token)

# Verifique se o token foi gerado
echo "TOKEN: $TOKEN"
```

---

### Passo 2: Cadastrar Insumos (Medicamentos)

Com o token, vamos cadastrar alguns insumos.

```bash
# Cadastrar Dipirona
curl -X POST http://localhost:8080/insumos \\
  -H "Authorization: Bearer $TOKEN" \\
  -H "Content-Type: application/json" \\
  -d '{
    "codigoCatmat": "34981",
    "nomeGenerico": "DIPIRONA SODICA",
    "formaFarmaceutica": "COMPRIMIDO",
    "marca": "Medley",
    "descricao": "500MG"
  }'

# Cadastrar Paracetamol
curl -X POST http://localhost:8080/insumos \\
  -H "Authorization: Bearer $TOKEN" \\
  -H "Content-Type: application/json" \\
  -d '{
    "codigoCatmat": "34933",
    "nomeGenerico": "PARACETAMOL",
    "formaFarmaceutica": "COMPRIMIDO",
    "marca": "EMS",
    "descricao": "750MG"
  }'
```

---

### Passo 3: Cadastrar Ponto de Dispensação

Vamos cadastrar um Ponto de Dispensação e **vincular ao nosso Client**.
*Nota: O `clientId` `1` corresponde ao `farmacia-central-app` inserido pelo Liquibase.*

```bash
curl -X POST http://localhost:8080/pontos-dispensacao \\
  -H "Authorization: Bearer $TOKEN" \\
  -H "Content-Type: application/json" \\
  -d '{
    "cnes": "1234567",
    "nome": "Farmácia Central de Testes",
    "tipo": "FARMACIA_CENTRAL",
    "emailResponsavel": "responsavel@farmacia.gov.br",
    "clientId": 1 
  }'
```

---

### Passo 4: Carga de Estoque (Bulk Upsert)

Agora, vamos enviar uma carga de estoque para o Ponto de Dispensação que acabamos de criar (ID `1`).

```bash
curl -X POST http://localhost:8080/estoque/carga `
  -H "Content-Type: application/json" `
  -H "Authorization: Bearer $TOKEN" `
  -d '{
    "cnesPontoDispensacao": "1234567",
    "dataCarga": "2026-02-03T10:30:00",
    "itens": [
      {
        "idInsumo": "INS001",
        "idLoteExterno": "LOTE-EXT-001",
        "numeroLote": "L123456",
        "dataValidade": "2027-12-31",
        "dataFabricacao": "2026-01-15",
        "quantidadeEnviada": 100,
        "quantidadeTotalLote": 500
      },
      {
        "idInsumo": "INS002",
        "idLoteExterno": "LOTE-EXT-002",
        "numeroLote": "L789012",
        "dataValidade": "2028-06-30",
        "dataFabricacao": "2026-02-01",
        "quantidadeEnviada": 50,
        "quantidadeTotalLote": 200
      }
    ],
    "insumosDetalhes": [
      {
        "id": "INS001",
        "nome": "Paracetamol 500mg",
        "formaFarmaceutica": "Comprimido",
        "marca": "Marca A",
        "descricao": "Analgésico e antipirético"
      },
      {
        "id": "INS002",
        "nome": "Dipirona 500mg",
        "formaFarmaceutica": "Comprimido",
        "marca": "Marca B",
        "descricao": "Analgésico e antitérmico"
      }
    ]
  }'
```

---

### Passo 5: Registrar Consumo

Finalmente, vamos simular o consumo de 5 unidades de Dipirona do lote que acabamos de inserir.
*Nota: O `pontoDispensacaoId` é `1` e o `loteId` também será `1` (o primeiro lote inserido).*

```bash
curl -X POST http://localhost:8080/api/v1/consumo \\
  -H "Authorization: Bearer $TOKEN" \\
  -H "Content-Type: application/json" \\
  -d '{
    "pontoDispensacaoId": 1,
    "listaConsumo": [
      {
        "loteId": 1,
        "quantidadeConsumida": 5,
        "dataHoraEvento": "2026-01-25T10:00:00Z"
      }
    ]
  }'
```

Após este comando, o saldo do lote `1` no inventário do ponto `1` deve ser `95`.
