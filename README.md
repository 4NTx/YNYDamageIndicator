# YNYDamageIndicator

Mod client-side para Stein Loader no Minecraft 1.8.9. Ao mirar em jogadores, mobs ou animais, exibe nome, vida atual/máxima, barra de vida e absorção.

## Recursos

- Alcance visual de até 32 blocos, respeitando paredes.
- HUD movível, escalável e ocultável pelo editor do Stein Loader.
- Opções para nome, números, absorção, alvos distantes, alcance visual, folhagem, cores e opacidade.
- Configurações salvas em `config/ynydamageindicator.json`.
- Não altera alcance de ataque, cliques ou pacotes.

## Instalação

1. Baixe `YNYDamageIndicator.steinmod` na página de Releases.
2. Coloque o arquivo em `.minecraft/mods`.
3. Inicie o Minecraft 1.8.9 pelo perfil Stein Loader.

Use o editor de HUD para posicionar o indicador. Abra o painel do Stein Loader com Right Shift para alterar as opções.

## Compilação

Requer JDK 25 e o [Stein SDK](https://github.com/x4vieer/stein-sdk).

```powershell
java -jar stein-sdk.jar setup --mc C:\caminho\para\.minecraft
java -jar stein-sdk.jar build --mc C:\caminho\para\.minecraft
```

O artefato será criado em `build/YNYDamageIndicator.steinmod`.

## Licença

[MIT](LICENSE)
