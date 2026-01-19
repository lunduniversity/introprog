# Instruktioner

I denna kurs använder vi programmeringsspråket **Scala** med olika programmeringsverktyg. Läs om hur du använder dessa programmeringsverktyg i **Appendix** i [kompendiet](https://fileadmin.cs.lth.se/pgk/compendium.pdf/). Verktygen finns förinstallerade på LTH:s [Linuxdatorer i E-huset](https://fileadmin.cs.lth.se/cs/Bilder/Salar/Datorsalar_E-huset.pdf). [Programmera på LTH:s Datorer](#programmera-på-lths-datorer)

Tips om du ska köpa egen dator finns under rubriken "Hårdvara".

## Programmera på din egen dator

Du behöver installera:
* **OpenJDK**; 
* **Scala** 
* **VS Code** med tillägget **Scala (Metals)**. 

Följ instruktionerna noga nedan **under rubriken för ditt operativsystem** om hur du installerar verktygen på din egen dator.

### Välj:

* [Windows](#windows)
* [MacOS](#macos)
* [Linux](#linux-ubuntu)
* [Windows Subsystem for Linux](#wsl)
* [Dual Booting](#dual-booting)
* [SDKMAN (Linux och MACOS)](#sdkman)
* [Hårdvara](#hårdvara)

Om det uppkommer problem kan du få hjälp med detta under de första 2 veckorna av kursen på luncherna i E:2116. Dessutom kommer du ha många duktiga kurskamrater som har erfarenhet av datorer. Passa på att fråga de för hjälp och på så viss skapa nya vänner.

## WINDOWS

* Innan du installerar ta reda på vilken typ av dator du har, **x86** eller **ARM**. De vanligaste är **x86** Du hittar information i [Settings -> System -> About](ms-settings:about). Läs vidare [här](https://support.microsoft.com/en-us/windows/find-information-about-your-windows-device-a66d52c8-3323-44fd-8f34-a9497bb935e1).

* Slå på att visa dolda filer och filtyp. Öppna Explorer, gå under View, slå på visa filtyp (show file extension), visa dolda filer (show hidden files) och visa sökväg (show search path). Läs vidare [här](https://support.microsoft.com/en-us/windows/file-explorer-in-windows-ef370130-1cca-9dc5-e0df-2f7416fe1cb1).

### 1. Terminalfönster

* För terminal i Windows rekommenderas "Windows Terminal". I Windows 11 är Windows Terminal redan standard. Om den finns inte installerat, [tryck här](https://learn.microsoft.com/en-us/windows/terminal/install) och följ instruktionerna.

* För generell användning kan du hitta de olika kommando som finns i gamla cmd: [https://ss64.com/nt/](https://ss64.com/nt/) och motsvarande i nyare powershell(PS): [https://ss64.com/ps/](https://ss64.com/ps/).

* ***Om du föredrar att använda en Unix-terminal***: *Git Bash* är ett terminalprogram som följer med när du installerar Git (se nedan). Den erbjuder grundläggande Linux-kommandon och fungerar bra för denna kurs.

### 2. Installera OpenJDK

* Du kanske redan har JDK installerat. Kontrollera detta genom att i ett terminalfönster skriva (observera avslutande c:et):

  ```
  javac --version
  ```

  Om utskriften säger att `javac` saknas, installera enligt instruktionerna nedan. 
  
  Om det anger en äldre version än version **25**, avinstallera den gamla versionen, sedan följ instruktionerna nedan.

* Installera OpenJDK för ditt system härifrån: [https://adoptium.net/](https://adoptium.net/)

    1. Välj Download Temurin.
    2. Dubbelklicka på filen som laddas ned för att starta installationen. 
    >Om du får en varning ska du köra ändå genom att klicka på "Mer information" eller liknande, och `run anyway`. 
    3. **VIKTIGT**: Under installationen, tryck på och välj **Install Entire Feature** för alla dessa alternativ: 
    - Modify PATH variable, 
    - Associate .jar, 
    - Set or override JAVA_HOME variable, JavaSoft (Oracle) registry key.
    3. Starta om din dator.
    4. Starta terminalfönster och kontrollera att `javac --version` ger rätt version.

### 3. Installera Scala med tillhörande verktyg

1. Följ ett av stegen nedan beroende på vilken dator du har:
  * **x86**: Om du har en x86-dator (vanligast): Ladda ned filen [`cs-x86_64-pc-win32.zip`](https://github.com/coursier/coursier/releases/latest/download/cs-x86_64-pc-win32.zip). Spara den på valfritt ställe och dubbel-klicka på den när nedladdningen är klar så den packas upp och dubbel-klicka på filen inuti för att köra installationsprogrammet. Följ instruktionerna och svara jakande. 
      >Om du får varningar så kör ändå genom att klicka "Mer information" eller liknande, och `run anyway`.
  * **ARM**: Om du har en ARM-dator (t.ex. Snapdragon eller liknande, inte så vanligt) kör istället dessa kommandon i terminalen (be om hjälp om du inte får igång terminalen eller om du får felmeddelande etc.):
    * Om du kör Windows Terminal klistra in dessa kommandon ett i taget:

      ```
      Start-BitsTransfer -Source https://github.com/coursier/launchers/raw/master/coursier -Destination coursier
      ```

      ```
      Start-BitsTransfer -Source https://github.com/coursier/launchers/raw/master/coursier.bat -Destination coursier.bat
      ```

      ```
      .\coursier setup
      ```

    * Om du kör Cmd klistra in dessa kommandon ett i taget:

      ```
      bitsadmin /transfer downloadCoursierCli https://github.com/coursier/launchers/raw/master/coursier "%cd%\coursier"
      ```

      ```
      bitsadmin /transfer downloadCoursierBat <https://github.com/coursier/launchers/raw/master/coursier.bat> "%cd%\coursier.bat"
      ```

      ```
      .\coursier setup
      ```

2. Starta om din dator.
3. Testa att skriva `scala --version` i ett nytt terminalfönster och om allt gått bra så ska du få en utskrift som börjar med "Scala code runner version 3". 
    > Om du får `[warning] MainGenericRunner 3.7.2` skriv `cs install scala:3.7.2` eller vilken version står i felmeddelanden och sedan ska `scala --version` fungera utan varning. 

### 4. Installera VS Code med tillägget **Scala (Metals)**

1. [Tryck här](https://code.visualstudio.com/Download) för att ladda ner och installera VSCode. 
2. Installera tillägget **Scala Metals**. Sök "Scala Metals" i Extesnions (ikonen med 4 lådor till vänster) och klicka Install. Alternativ skriv i terminalen 
    ```
    code --install-extension scalameta.metals --force
    ```
3. Första gången ett projekt öppnas i VS Code så tar det ett tag innan Metals har byggt allt från grunden. Du kan följa vad som händer i meddelandefältet längst ned; allt är klart när det står "Index complete" efter en raket-ikon.
4. Om VS Code varnar om fel eller aldrig slutar indexera, kan det vara bra att stänga ner VS Code och ta bort dessa underkataloger (om de existerar): `.bsp .bloop .vscode .metals .scala-build target` och därefter starta om VSCode.

### 5. Installera Kojo

Vi använder Kojo på första labben. Kojo är utvecklat speciellt för att hjälpa elever i grundskola och gymnasium att lära sig programmera.

1. Använd **grafikbiblioteket i kojo**. Ladda ner filen [https://fileadmin.cs.lth.se/kojo.scala](https://fileadmin.cs.lth.se/kojo.scala) till en mapp.

2. Öppna mappen i VSCode, och kör enl. instruktioner i kompendiet, t.ex. med `scala repl .`

Kojo används på [Vattenhallen Science Center](https://www.vattenhallen.lu.se/upplevelser/programmering/). LTH-studenter med programmeringskunskaper och intresse för pedagogik är välkomna att ansöka om att bli programmeringshandledare i Vattenhallen här: [https://www.vattenhallen.lu.se/om-oss/kontakt/vh-student/student-intresseanmalan/](https://www.vattenhallen.lu.se/om-oss/kontakt/vh-student/student-intresseanmalan/)

### 6. Installera Git

1. Ladda ner Git: [https://git-scm.com/download/win](https://git-scm.com/download/win), välj antingen **Windows/x64 Setup** för **x86** eller **Windows/ARM64 Setup** för **ARM**.
2. Dubbelklicka på den nedladdade filen för att starta installationen. Följ instruktionerna och välj de förvalda alternativen om du är osäker.
3. Vid val av editor, välj gärna *nano* (enkel texteditor direkt i terminalen, smidigt för Git).
4. När installationen är klar, öppna Git Bash (detta är en terminal som följer med Git-installationen).
5. Skriv `git --version` för att kontrollera att Git är korrekt installerat.


## MacOS

* Innan du installerar ta reda på vilken typ av dator du har. Öppna ett terminalfönster (se nedan) och skriv `uname -m` och se om du har `x86` eller `ARM` och notera detta (det påverkar hur du ska installera grejer i efterföljande steg).

* Därefter slå på att visa filtyp och dolda filer. Öppna ett terminalfönster (se nedan) och klistra in dessa kommandon:

```
defaults write NSGlobalDomain AppleShowAllExtensions -bool true && killall Finder
defaults write com.apple.finder AppleShowAllFiles YES && killall Finder
```

### 1. Terminalfönster

* Följ instruktioner här: [https://www.howtogeek.com/682770/how-to-open-the-terminal-on-a-mac/](https://www.howtogeek.com/682770/how-to-open-the-terminal-on-a-mac/)

* I senare versioner av macOS är *zsh* standardkommandotolk istället för *bash*, men det mesta fungerar lika. Det går alltså bra att ha det som det är men vill du byta till bash så följ instruktionerna nedan under rubriken *Alternativ terminal på MacOS (Valfri)* nedan.

### 2. Installera Homebrew (pakethanterare)

Homebrew är det enklaste sättet att installera program och verktyg på macOS. Öppna ett terminalfönster och kör kommando:

```bash
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
```

När installationen är klar, följ de instruktioner som skrivs ut för att lägga till Homebrew i din PATH.

Kontrollera att Homebrew fungerar genom att köra kommando:

```bash
brew --version
```

#### Alternativ terminal (Valfri)

MacOS kommer med en inbyggd terminal, men många föredrar att använda iTerm2 som är mer flexibel.

* Installera iTerm2 (valfritt) med:

    ```bash
    brew install --cask iterm2
    ```

* Installera Bash
    Apple använder zsh som standard, men våra instruktioner förutsätter bash. Installera en ny version av bash via Homebrew:

    ```bash
    brew install bash
    ```

* Byt sedan standardskal till bash:

    ```bash
    chsh -s /opt/homebrew/bin/bash
    ```

* Stäng terminalen och öppna en ny för att ändringarna ska träda i kraft. Förslagsvis kan du använda iTerm2 istället för vanliga terminalen.

### 3. Installera OpenJDK

* Du kanske redan har JDK installerat. Kontrollera detta genom att i ett terminalfönster klistra:

  ```
  javac --version
  ```

  Om utskriften säger att `javac` saknas eller anger en annan version än version 25, installera då OpenJDK enl. nedan.

1. Installera OpenJDK med hjälp av Homebrew genom kommando:

    ```bash
    brew install openjdk@25
    ```

2. Lägg till JDK i din miljö (så att javac hittas i PATH och JAVA_HOME sätts):

    ```bash
    echo 'export PATH="/opt/homebrew/opt/openjdk@21/bin:$PATH"' >> ~/.bash_profile
    echo 'export JAVA_HOME=$(/usr/libexec/java_home -v 25)' >> ~/.bash_profile
    source ~/.bash_profile
    ```

3. Kontrollera att installationen fungerat:

    ```bash
    javac --version
    ```

### 4. Installera Scala med tillhörande verktyg

  * Installera Scala via Homebrew:

      ```
      brew install scala
      ```

  * Installera Scala CLI  via Homebrew

      ```
      brew install Virtuslab/scala-cli/scala-cli
     ```

  * Kontrollera att installationen fungerar:

      ```
      scala --version
      ```

### 5. Installera VS Code med tillägget "Scala (Metals)"

1. [Tryck här](https://code.visualstudio.com/Download) och ladda ner VSCode eller genom kommando 
    ```
    brew install --cask visual-studio-code
    ```
2. Installera tillägget **Scala Metals**. Sök "Scala Metals" i Extesnions (ikonen med 4 lådor till vänster) och klicka Install. Alternativ skriv i terminalen 
    ```
    code --install-extension scalameta.metals --force
    ```
3. Första gången ett projekt öppnas i VS Code så tar det ett tag innan Metals har byggt allt från grunden. Du kan följa vad som händer i meddelandefältet längst ned; allt är klart när det står "Index complete" efter en raket-ikon.
4. Om VS Code varnar om fel eller aldrig slutar indexera, kan det vara bra att stänga ner VS Code och ta bort dessa underkataloger (om de existerar): `.bsp .bloop .vscode .metals .scala-build target` och därefter starta om VSCode.

### 6. Installera Kojo

Vi använder Kojo på första labben. Kojo är utvecklat speciellt för att hjälpa elever i grundskola och gymnasium att lära sig programmera.

1. Använd **grafikbiblioteket i kojo**. Ladda ner filen [https://fileadmin.cs.lth.se/kojo.scala](https://fileadmin.cs.lth.se/kojo.scala) till en mapp.

2. Öppna mappen i VSCode, och kör enl. instruktioner i kompendiet, t.ex. med `scala repl .`

Kojo används på [Vattenhallen Science Center](https://www.vattenhallen.lu.se/upplevelser/programmering/). LTH-studenter med programmeringskunskaper och intresse för pedagogik är välkomna att ansöka om att bli programmeringshandledare i Vattenhallen här: [https://www.vattenhallen.lu.se/om-oss/kontakt/vh-student/student-intresseanmalan/](https://www.vattenhallen.lu.se/om-oss/kontakt/vh-student/student-intresseanmalan/)

#### 7. Installera Git

* När du har Homebrew installerat, öppna ett terminalfönster och skriv följande kommando för att installera Git:

    ```
    brew install git
    ```

## LINUX (Ubuntu)

1. Innan du installerar är det bra om du först tar reda på vilken typ av dator du har. Öppna ett terminalfönster (se nedan) och skriv `uname -m` och se om du har `x86` eller `ARM` och notera detta (det påverkar hur du ska installera grejer i efterföljande steg).

2. Därefter är det bra om du slår på att visa dolda filer och filtyp. Öppna ett terminalfönster (se nedan) och klistra in detta kommando och tryck enter:

    ```
    gsettings set org.gtk.gtk4.Settings.FileChooser show-hidden true
    gsettings set org.gtk.gtk4.Settings.FileChooser show-type-column true
    ```

### 1. Starta terminalfönster

* Tryck Ctrl+Alt+T eller tryck på Windows-tangenten och sök efter "Terminal".

### 2. Installera OpenJDK

* Du kanske redan har JDK installerat. Kontrollera detta genom att i ett terminalfönster skriva (observera avslutande c:et):

  ```
  javac --version
  ```

  Om utskriften säger att `javac` saknas eller anger en annan version än version 25, installera då OpenJDK enl. nedan.

* Om du inte redan har OpenJDK version 25, öppna terminalfönster och uppdatera Ubuntu till den senaste version:
    ```
    sudo apt update && sudo apt full-upgrade -y
    ```
* Installera OpenJDK:
    ```
    sudo apt install openjdk-25-jdk openjdk-25-doc openjdk-25-source
    ```
* För andra distributioner, se relevant dokumentation.

### 3. Installera Scala med tillhörande verktyg

* Testa om nedladdningsprogrammet `curl` finns på ditt system genom att klistra i terminalen:
```
curl --version
``` 
Om `curl` saknas så installera detta i terminalen genom att skriva: 
```
sudo apt install curl
```

1. Installera Scala-verktygen genom att klistra in detta i din terminal:
    * Om du har en x86-dator (vanligast):

      ```
      curl -fL https://github.com/coursier/coursier/releases/latest/download/cs-x86_64-pc-linux.gz | gzip -d > cs && chmod +x cs && ./cs setup
      ```
    
    * Om din dator har en processor med ARM64-arkitektur (inte så vanligt) använd i stället följande kommando:

      ```
      curl -fL https://github.com/VirtusLab/coursier-m1/releases/latest/download/cs-aarch64-pc-linux.gz | gzip -d > cs && chmod +x cs && ./cs setup
      ```
      Båda kommandon återfinns även under *"Linux"* på [https://www.scala-lang.org/download/](https://www.scala-lang.org/download/).

2. Svara med stort Y för ja på eventuell fråga om att addera coursier till din path.
3. Starta om din dator.
4. Klistra i ett nytt terminalfönster: 
```
scala --version
```  
Om allt gått bra så ska du få en utskrift som börjar med "Scala code runner version 3". 

Om du får `[warning] MainGenericRunner` skriv `cs install scala:3.7.2` och sedan ska `scala --version` fungera utan varning.

### 4. Installera VS Code med tillägget "Scala (Metals)"

1. [Tryck här](https://code.visualstudio.com/Download) och ladda ner VSCode.
2. Installera tillägget **Scala Metals**. Sök "Scala Metals" i Extesnions (ikonen med 4 lådor till vänster) och klicka Install. Alternativ skriv i terminalen:
    ```
    code --install-extension scalameta.metals --force
    ```
3. Första gången ett projekt öppnas i VS Code så tar det ett tag innan Metals har byggt allt från grunden. Du kan följa vad som händer i meddelandefältet längst ned; allt är klart när det står "Index complete" efter en raket-ikon.
4. Om VS Code varnar om fel eller aldrig slutar indexera, kan det vara bra att stänga ner VS Code och ta bort dessa underkataloger (om de existerar): `.bsp .bloop .vscode .metals .scala-build target` och därefter starta om VSCode.

### 5. Installera Kojo enligt instruktioner längre ner under rubriken "KOJO"

Vi använder Kojo på första labben. Kojo är utvecklat speciellt för att hjälpa elever i grundskola och gymnasium att lära sig programmera.

1. Använd **grafikbiblioteket i kojo**. Ladda ner filen [https://fileadmin.cs.lth.se/kojo.scala](https://fileadmin.cs.lth.se/kojo.scala) till en mapp.

2. Öppna mappen i VSCode, och kör enl. instruktioner i kompendiet, t.ex. med `scala repl .`

Kojo används på [Vattenhallen Science Center](https://www.vattenhallen.lu.se/upplevelser/programmering/). LTH-studenter med programmeringskunskaper och intresse för pedagogik är välkomna att ansöka om att bli programmeringshandledare i Vattenhallen här: [https://www.vattenhallen.lu.se/om-oss/kontakt/vh-student/student-intresseanmalan/](https://www.vattenhallen.lu.se/om-oss/kontakt/vh-student/student-intresseanmalan/)

### 6. Installera Git

Klistra i din terminal:

```
apt-get install git
```

## WSL

#### Windows Subsystem On Linux (Ubuntu on Windows)
* ***(Rekommenderas om du vill ha riktigt Linux men behålla fortfarande Windows)*** Om du har en uppdaterad version av Windows 10 eller 11 så kan du köra **WSL2**, som ger dig tillgång till Linux/Ubuntu direkt under Windows i ett separat filsystem. 

1. Starta Windows Terminal med admin-rättigheter så här: tryck på windows-knappen, skriv `Windows Terminal`, högerklicka på den och välj run as administrator. Följ vidare instruktioner [Tryck Här](https://docs.microsoft.com/en-us/windows/wsl/install)

2. Inom Terminalen, följ nu instruktionerna under [Linux](#linux-ubuntu) istället för Windows.

3. Installera VSCode på Windows med tillägget Remote Explorer från Microsoft.
* [Tryck här](https://code.visualstudio.com/docs/remote/wsl) och följ instructionernerna för att köra VSCode i Windows genom WSL.

4. Om du vill få ljud att fungera då du kör program inifrån WSL, så kan du ta hjälp av guiden ``sound_in_wsl2.md`` [https://github.com/lunduniversity/introprog/blob/master/web/tools/sound_in_wsl2.md](https://github.com/lunduniversity/introprog/blob/master/web/tools/sound_in_wsl2.md)


## Dual Booting

Du kan helt [ersätta Windows med Ubuntu](https://ubuntu.com/tutorials/install-ubuntu-desktop). Det rekommenderas om du har en äldre dator, eftersom prestandan på linux är bättre. 

Alternativ kan du installera Ubuntu på en del av din befintlig SSD, eller på en separat SSD i din dator, och bevara Windows genom Dual Booting. [Tryck här](https://medium.com/linuxforeveryone/how-to-install-ubuntu-20-04-and-dual-boot-alongside-windows-10-323a85271a73) och följa tutorialen.

## SDKMAN

#### Linux/Ubuntu/WSL: Installera SDKMAN

[https://sdkman.io/](https://sdkman.io/) är ett populärt installationsverktyg för att enkelt installera och hantera olika versioner av allehanda programmeringsverktyg för Ubuntu/Linux/WSL. För dig som hellre vill använda SDKMAN i stället så går det utmärkt att installera Scala-verktugen ovan med hjälp av nedan kommando ett i taget i tur och ornding (om du har [installerat SDKMAN](https://sdkman.io/install)) och svara med stort Y på eventuella frågor om att göra nya versionen default:

```
sdk update
sdk install java 25
sdk install scala
sdk install scalacli
sdk install sbt
```

*Tips:* Det är viktigt att du noterar HUR du har installerat olika grejer på din dator, speciellt om du blandar olika metoder. Om du behöver uppdatera eller avinstallera så blir det lätt förvirring om du glömt hur du installerat och försöker uppdatera/avinstallera med annan metod än du installerat etc.

Läs mer om vad du kan göra med en VS Code och andra verktyg i appendix i [kompendiet](https://fileadmin.cs.lth.se/pgk/compendium.pdf/)

## Programmera på LTH:s datorer

På LTH:s [Linuxdatorer i E-huset](https://fileadmin.cs.lth.se/cs/Bilder/Salar/Datorsalar_E-huset.pdf) finns alla dessa verktyg förinstallerade:

* Rekommenderad kodeditor: VS Code startas med kommandot `code .` (Tillägget **Scala Metals** är förinstallerat.)  
* Scala REPL: `scala repl .`
* Scala-kompilatorn: `scala compile .`.
* Scala kompilering och programkörning: `scala .`
* Utvecklingsmiljön för labb w01 Kojo: `kojo`
* Byggverktyget Scala Build Tool: `sbt`
* Java-kompilatorn: `javac`
* Exekveringsmijlön JVM med tillhörande utvecklingsbibliotek (OpenJDK): `java`

Mer info [om skolans datorer här](https://www.lth.se/lthin/datorsalar/vaara-datorsalar/e-huset/) och [hur du använder Linux](http://fileadmin.cs.lth.se/cs/Education/EDAA60/general/unix-x.pdf) och [hur du använder bash](https://github.com/RehanSaeed/Bash-Cheat-Sheet).


## Hårdvara

Vid undervisning på campus rekommenderas LTH:s [Linux-datorer](https://www.lth.se/lthin/datorsalar/vaara-datorsalar/e-huset/). 

När du studerar hemma behöver du en bra arbetsplats och en dator med Linux (t.ex. Ubuntu), Windows eller MacOS. Det är bra att ha en ergonomisk kontorsstol vid ett bekvämt skrivbord, ett flyttbart tangentbord, en separat mus och en höj-och-sänkbar skärm.

### Köpa egen dator

När du ska programmera är det bra med en dator med minst 16GB RAM och Ryzen processor.  

För en bra dator som kommer hålla hela utbildningen rekommenderas Lenovo Thinkpad, men det är dyrare än andra varianter.
Begagnade Lenovo är prisvärda och de är kontrollerade innan försäljning med garanti från sajter som specialiserar sig på begagnat: 
* [Inrego](https://shop.inrego.se/) Prisvärda val är [Thinkpad T14](https://shop.inrego.se/Shop/Product/List?q=T14) som har matt skärm utan reflexer och bra tangentbord och kostar från ca 4500kr - vill du lägga lite mer så uppgradera SSD till 480GB (+1000 kr).
* [Inet](https://www.inet.se/kategori/1323/refurbished-begagnad/521/lenovo)
* [BilligTeknik](https://www.billigteknik.se/985-lenovo-thinkpad)

Om du istället föredrar en ny dator, både [Lenovo](https://www.lenovo.com/se/sv/studentrabatt/) och [Dell](https://www.dell.com/sv-se/shop/dell-advantage/cp/students) har studentrabatt och säljer nya datorer. Lokala datorbutiker finns här: [Inet i Malmö](https://www.inet.se/), [Webhallen i Malmö](https://www.webhallen.com/), [Compliq i Lund](https://www.compliq.se/bygga-dator)

Om du letar efter en bra stationär dator som är fläktlös och därför *helt tyst* rekommenderas [AtLast Solutions](https://www.atlastsolutions.com/) som även erbjuder Ubuntu förinstallerat utan extra kostnad.

### Headset-adapter

Vid distansundervisning är det bra med headset och webbkamera. Det går bra att koppla ditt eget headset till skolans datorer, t.ex. om du behöver prata med en handledare som jobbar hemifrån. Enklast är att använda ett USB-headset då kontakten sitter på framsidan. Om du har ett headset med 4-polig mobil-kontakt medan din dator har två 3-poliga kontakter för mic och hörlurar behövs en sådan här adapter:

<img src="https://github.com/lunduniversity/introprog/raw/master/web/tools/adapter.jpg" alt="drawing" style="width:200px;"/>

Kan t.ex. köpas här:
[NetOnNet](https://www.netonnet.se/art/ljud-bild/kablar/adapter/andersson-computer-headset-adapter-female/1006756.13721/),
[Elgiganten](https://www.elgiganten.se/product/ljud-hifi/ljudkablar-adapters/HAMA54572/hama-3-5-mm-adapter-for-headset-med-mikrofon),
[Webhallen](https://www.webhallen.com/se/product/313979-iiglo-Multimedia-adapter-till-Dator-Svart),
[Kjell](https://www.kjell.com/se/produkter/dator/horlurar-headset/tillbehor-for-horlurar-headset/datoradapter-for-mobil-headset-p39356),
[Inet](https://www.inet.se/produkt/8904289/deltaco-adapter-2x3-5mm-ha-till-3-5mm-ho-4-pin-0-1m-svart)
