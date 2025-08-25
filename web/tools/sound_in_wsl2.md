# Ljud i WSL2

Detta är en guide för att fixa problem med att ljud inte spelas upp från applikationer som körs under WSL2. Notera att det kan vara så att ljud redan fungerar för dig i WSL2. Detta beror på vilken version av WSL2 du kör och huruvida utvecklarna av WSL2 numera har fixat detta problem. Testa därför alltid först ifall du verkligen har problem med ljudet, innan du ger dig på stegen i denna guide. Ett alternativ är också att istället köra vad det nu är för någonting du håller på med direkt under Windows istället för i WSL2, men om du föredrar att göra vad som krävs för att få ljud att fungera i WSL2, kan denna guide hjälpa dig.

Den här guiden är baserad på en guide skriven av Pedro Augusto Fabri, vilken kan återfinnas här: <https://www.linkedin.com/pulse/sound-wsl2-windows-11-pulseaudio-configuration-made-easy-pedro-fabri-fu9mf/>

Guiden är i grunden samma som Fabris ursprungliga guide, men översatt till svenska, och med några mindre uppdateringar, förenklingar, och förtydliganden.

// Hälsningar från Elias Åradsson som skrev ursprungsversionen av detta dokument

## Steg 1 - Skaffa PulseAudio för Windows

Ladda ner zip-filen med PulseAudio från den officiella webbplatsen här: <https://www.freedesktop.org/wiki/Software/PulseAudio/Ports/Windows/Support/>

Extrahera zip-filen till ``C:\pulseaudio``.

## Steg 2 - Konfigurera PulseAudio

### 2.1 Konfigurera serverns IP

Gå till ``C:\pulseaudio``, och öppna etc/pulse/default.pa i en editor och ändra rad 61 från

``#load-module module-native-protocol-tcp``

till detta:

``load-module module-native-protocol-tcp auth-ip-acl=127.0.0.1 auth-anonymous=1``

### 2.2 Förhindra servern från att stängas ner

Öppna etc/pulse/daemon.conf och ändra rad 39 från detta:

``; exit-idle-time = 20``

till detta:

``exit-idle-time = -1``

## Steg 3 - Konfigurera PulseAudio i WSL2

### 3.1 Installera PulseAudio

Installera PulseAudio i WSL2 med hjälp av följande kommandon:

```
sudo apt update
sudo apt install pulseaudio pulseaudio-utils
```

### 3.2 Ta fram IP-addressen i Windows

Öppna en Windows-terminal (PowerShell/CMD) och skriv detta kommando:

``ipconfig``

Leta upp din IP4-adress och kopiera den. Adressen bör återfinnas på detta vis:

```
Link-local IPv6 Address . . . . . . :
IPv4 Address. . . . . . . . . . . . : <adressen du ska kopiera>
Subnet Mask . . . . . . . . . . . . : 
```

### 3.3 Konfigurera PULSE_SERVER miljövariabeln

PulseAudio använder miljövariabeln PULSE_SERVER. Denna måste därför alltid peka på servern du vill koppla till, alltså behöver du här IP-adressen du kopierade i föregående steg.

För att du ska slippa sätta denna variabel varje gång du öppnar en ny Ubuntu-terminal, så vill du lägga till följande rader i din `.bashrc` (en konfigurationsfil som körs varje gång du öppnar en ny Ubuntu-terminal).

Säkerställ att du står i din hemkatalog i ditt Ubuntu-system: `cd`

Öppna `.bashrc` i en editor, förslagsvis Nano: `nano .bashrc`

Gå längst ner i denna fil och klistra in följande (antagligen med hjälp av Ctrl+Shift+V):

```
# Get current host IP
export HOST_IP=$(ipconfig.exe | grep "IPv4 Address" | awk '{print $NF}' | head -n 1)

# Configure PulseAudio Server
export PULSE_SERVER=tcp:$HOST_IP
```

Spara dina ändringar (om du använder Nano så trycker du Ctrl+X och följer sedan instruktionerna som visas på skärmen).

## Steg 4 - Testa

### 4.2 Öppna PulseAudio i Windows

Gå tillbaka till din Windowsterminal (PowerShell/CMD) och gå till ``C:\pulseaudio\bin``.

Starta PulseAudio genom att köra följande kommando:

.\pulseaudio.exe

Först gången du kör PulseAudio på detta vis kommer du förmodligen få upp en dialogruta där PulseAudio efterfrågar behörigheter. Ge PulseAudio alla behörigheter som programmet vill ha.

Andra gången du kör PulseAudio så bör du få en utskrift som ser ut ungefär så här:

```
W: [(null)] pulsecore/core-util.c: Secure directory creation not supported on Win32.
W: [(null)] pulsecore/core-util.c: Secure directory creation not supported on Win32.
W: [(null)] pulsecore/core-util.c: Secure directory creation not supported on Win32.
W: [(null)] pulsecore/pid.c: Stale PID file, overwriting.
W: [(null)] pulsecore/core.c: failed to allocate shared memory pool. Falling back to a normal memory pool.
W: [(null)] pulsecore/core-util.c: Secure directory creation not supported on Win32.
W: [(null)] pulsecore/core-util.c: Secure directory creation not supported on Win32.
W: [(null)] pulsecore/core-util.c: Secure directory creation not supported on Win32.
E: [(null)] daemon/main.c: Failed to load directory.
```

Dessa meddelanden kan se lite läskiga ut, men är alltså att förvänta.

### 4.3 Testa ljudet i WSL2

Du kan nu testa ljudet i WSL2 med valfritt program som du litar på kommer producera ljud i enlighet med dina förväntningar.

Vill du ha ett minimalt test, kan du till exempel använda SoX (The Sound eXchange).
Installera SoX med hjälp av följande kommando:

`sudo apt install sox`

Testa sedan genom att skriva följande kommando:

`play -n synth 10 sine 20-20000`

Om allt fungerar som det ska så kommer detta kommando producera en sinuston som ökar i frekvens fram tills den har ljudit klart.

## Slutkommentar

När du vill köra ljud inifrån WSL2 i fortsättningen så måste du alltså först öppna en Windowsterminal (PowerShell/CMD) och starta PulseAudio genom att köra ``C:\pulseaudio\bin\pulseaudio.exe``.

Därefter öppnar du helt enkelt WSL2 och ljud ska då fungera som tänkt!
