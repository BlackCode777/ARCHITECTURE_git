# Serviço 6 — Load Balance

> Parte de [ARQUITETURA-SERVICOS.md](ARQUITETURA-SERVICOS.md). Task 07 do roadmap
> ([FASE-1-FUNDAMENTOS.md](FASE-1-FUNDAMENTOS.md)).

## Conceito

* **Round Robin** — distribui requisições sequencialmente entre instâncias.
* **Least Connections** — envia a requisição para a instância com menos conexões ativas.
* **Sticky Session** — fixa um cliente sempre na mesma instância (útil para sessão em
  memória; evitar quando a aplicação é stateless).
* **Health Check** — o balanceador só envia tráfego para instâncias saudáveis
  (`/actuator/health`).
* **Reverse Proxy** — componente (ex.: Nginx) que recebe o tráfego externo e o
  redistribui internamente, escondendo a topologia real dos serviços.

## Estrutura de Pastas

```text
lb/
├── application/
│   └── InstanceIdProvider.java  ← resolve INSTANCE_ID da variável de ambiente
└── web/
    └── InstanceInfoController.java ← GET /lb/instance
```

## O que foi criado

* [InstanceIdProvider.java](../fundamentos-2semanas/src/main/java/com/tesseracode_labs/fundamentos_2semanas/lb/application/InstanceIdProvider.java) — lê `instance.id` (env `INSTANCE_ID`), definido por instância no `podman-compose.yml`.
* [InstanceInfoController.java](../fundamentos-2semanas/src/main/java/com/tesseracode_labs/fundamentos_2semanas/lb/web/InstanceInfoController.java) — `GET /lb/instance`, usado para confirmar visualmente qual instância atendeu cada requisição durante o teste de balanceamento.

## Próximos passos (Mini Projeto da Task 07)

* [ ] Subir duas instâncias da aplicação via Podman (`INSTANCE_ID=1` e `INSTANCE_ID=2`)
* [ ] Configurar Nginx como reverse proxy/load balancer (`podman-compose.yml`, ver
      seção "Infraestrutura Podman" do roadmap)
* [ ] Bater `GET /lb/instance` repetidamente e confirmar alternância entre instâncias
* [ ] Testar failover: derrubar uma instância e confirmar que o tráfego migra
* [ ] Medir tempo de resposta com e sem balanceamento

## Referências

* Martin Fowler — https://martinfowler.com
* Skill `escalabilidade-sistemas-distribuidos` (catálogo global) — load balancer, health check
