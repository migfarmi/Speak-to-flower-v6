# SpeakToFlower V5

SpeakToFlower V5 é um aplicativo Android desenvolvido em Kotlin que converte voz em texto e texto em fala.

O projeto começou utilizando o mecanismo TTS padrão do Android, mas está sendo expandido para oferecer suporte a vozes de IA através do Coqui XTTS v2.

##  Funcionalidades

-  Reconhecimento de voz (SpeechRecognizer)
 Conversão de voz para texto
-  Conversão de texto em fala
- Sistema de slots para diferentes vozes
-  Importação de modelos de voz
- Salvamento automático das configurações
-  Interface simples e leve
-  Suporte futuro ao Coqui XTTS v2

---

##  Tecnologias

- Kotlin
- Android SDK
- SpeechRecognizer
- Android TextToSpeech
- SharedPreferences
- MediaPlayer

Servidor (em desenvolvimento)

- Python
- Flask
- Coqui XTTS v2

---

##  Estrutura do Projeto

```
MainActivity.kt
VoiceManager.kt
VoiceSlot.kt
ApiClient.kt
activity_main.xml
AndroidManifest.xml
```

---

## Em desenvolvimento

- Integração completa com Coqui XTTS v2
- Clonagem de voz
- Melhor gerenciamento de modelos
- Reprodução de áudio em streaming
- Configurações avançadas

---

##  Como executar

1. Abra o projeto no Android Studio ou CodeAssist.
2. Compile o aplicativo.
3. Conceda a permissão do microfone.
4. Pressione **Falar**.
5. O texto reconhecido aparecerá na tela.
6. Pressione **Falar Voz** para reproduzir o texto.

---

## 🖥️ Servidor XTTS

O aplicativo poderá utilizar um servidor Python para gerar vozes utilizando o Coqui XTTS v2.

O servidor receberá o texto enviado pelo aplicativo e retornará um arquivo WAV gerado por IA.

---

##  Status

Desenvolvimento ativo.

---

## Licença

Este projeto é distribuído sob a licença MIT.

---

## Autor

Desenvolvido por mig farmi

com revisão do chat gpt open ai
