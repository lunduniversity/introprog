I denna kurs använder vi programmeringsspråket **Scala** och olika programmeringsverktyg. Läs om hur du använder dessa programmeringsverktyg i **Appendix** i [kompendiet](https://fileadmin.cs.lth.se/pgk/compendium.pdf/). Verktygen finns förinstallerade på LTH:s [Linuxdatorer i E-huset](https://fileadmin.cs.lth.se/cs/Bilder/Salar/Datorsalar_E-huset.pdf). Instruktioner om hur du installerar dessa verktyg på din egen dator finns nedan.

Tips om du ska köpa egen dator finns längre ned under rubriken "Hårdvara".

## Programmera på LTH:s datorer

På LTH:s [Linuxdatorer i E-huset](https://fileadmin.cs.lth.se/cs/Bilder/Salar/Datorsalar_E-huset.pdf) finns alla dessa verktyg förinstallerade:

* Rekommenderad kodeditor: VS Code startas med kommandot `code .` (Tillägget `Scala Metals` är förinstallerat.)  
* Scala REPL: `scala` eller `scala-cli repl .`
* Scala-kompilatorn: `scalac`, `scala compile` eller `scala-cli compile .`
* Nybörjarvänliga utvecklingsmiljön Kojo: `kojo`
* Byggverktyget Scala Build Tool: `sbt`
* Java-kompilatorn: `javac`
* Exekveringsmijlön JVM med tillhörande utvecklingsbibliotek (OpenJDK): `java`
* Utvecklingsmiljön IntelliJ IDEA med Scala-plugin: `idea`

Mer info [om skolans datorer här](https://www.lth.se/lthin/datorsalar/vaara-datorsalar/e-huset/) och [hur du använder Linux](http://fileadmin.cs.lth.se/cs/Education/EDAA60/general/unix-x.pdf) och [hur du använder bash](https://github.com/RehanSaeed/Bash-Cheat-Sheet).


## Programmera på din egen dator 

Du behöver installera detta på din egen dator: **OpenJDK**, **Scala** och **VS Code** med tillägget Scala (Metals). Följ instruktionerna noga nedan **under rubriken för ditt operativsystem** om hur du installerar verktygen på din egen dator. Du kan läsa mer om hur du använder dessa verktyg i Appendix i [kompendiet](https://fileadmin.cs.lth.se/pgk/compendium.pdf/). 

Många av de programmeringsverktyg vi använder körs via ett terminalfönster. Hur du får igång terminalen beskrivs nedan.

Scala med tillhörande verktyg installeras med hjälp av det officiella installationsverktyget Coursier enligt nedan. Du får då bl.a. följande terminalverktyg som du kan läsa om i kompendiet: `scala`, `scalac`, `scala-cli`, `scaladoc`, `sbt`. 

Om du kör MacOS eller Linux/Ubuntu/WSL så kan du gärna i stället använda den smidiga pakethanteraren SDKMAN, läs mer nedan under rubriken "SDKMAN" längre ned.

Om redan har en **gammal installation** av Scala-verktyg så **avinstallera först** med detta kommando: `cs uninstall --all` och **starta om** din dator efter det, innan du sedan följer efterföljande instruktioner för ditt operativsystem. 

Om du kör en gammal version av SDKMAN rekommenderas att köra `sdk selfupdate force` och sedan starta om innan du installerar nya versioner enligt instruktioner under rubriken "SDKMAN" längre ned.

### WINDOWS

#### Terminalfönster i Windows

För terminal i Windows rekommenderas Microsoft-appen "Windows Terminal", se vidare här: [https://docs.microsoft.com/en-us/windows/terminal/get-started](https://docs.microsoft.com/en-us/windows/terminal/get-started). Det räcker fint med Windows Terminal för att köra alla kursens verktyg i cmd eller powershell, men det finns också andra sätt att köra terminal i Windows som beskrivs i punktlistan nedan. 

* Du kan också köra den befintliga men antika terminalen som inte kräver någon installation, följ instruktioner här [https://www.howtogeek.com/235101/10-ways-to-open-the-command-prompt-in-windows-10/](https://www.howtogeek.com/235101/10-ways-to-open-the-command-prompt-in-windows-10/) 
 
* Om du har en uppdaterad version av Windows 10 eller 11 så kan du (efter lite meckande, ev. med lite hjälp av en van windowsanvändare) köra **WSL**, helst version 2, som ger dig tillgång till Linux/Ubuntu direkt under Windows i ett separat filsystem. För att installera WSL behöver du starta  **PowerShell** med admin-rättigheter så här: tryck på windows-knappen, skriv `powershell` och högerklicka och välj run as administrator. Följ vidare instruktioner här och välj att installera Ubuntu 22.04 (fråga ngn om hjälp om du kör fast):  [https://docs.microsoft.com/en-us/windows/wsl/install-win10](https://docs.microsoft.com/en-us/windows/wsl/install-win10) När du installerat WSL följ instruktionerna under **Linux-rubrikerna** nedan för att installera kursens programmeringsverktyg under WSL/Ubuntu. Om du kör WSL2 kan då även få fönster att fungera om du följer dessa instruktioner: [https://learn.microsoft.com/en-us/windows/wsl/tutorials/gui-apps](https://learn.microsoft.com/en-us/windows/wsl/tutorials/gui-apps)  
WSL kan krångla så om du inte får det att funka så installera alla kursens verkyg direkt under Windows så du kommer igång och be någon om hjälp vid senare tillfälle.

* Det finns även andra sätt att installera Linux/Ubuntu, t.ex. med [VirtualBox](https://ubuntu.com/tutorials/how-to-run-ubuntu-desktop-on-a-virtual-machine-using-virtualbox#1-overview) (kräver snabb dator och rejät med RAM) eller [Dual Boot](https://medium.com/linuxforeveryone/how-to-install-ubuntu-20-04-and-dual-boot-alongside-windows-10-323a85271a73) (kräver att du har minst 25GB extra utrymme på disken, gärna mer, och att du krymper din Windows-partition.), eller att du helt [ersätter Windows med Ubuntu](https://ubuntu.com/tutorials/install-ubuntu-desktop) (enklaste sättet och funkar fint även på äldre och klenare datorer). Fråga någon medstudent som vet hur det går till om råd och hjälp om du vill installera Ubuntu "på riktigt". 

Här finns listor med olika kommando som finns i gamla cmd: [https://ss64.com/nt/](https://ss64.com/nt/) och motsvarande i nyare powershell finns här: [https://ss64.com/ps/](https://ss64.com/ps/).


#### Installera OpenJDK i Windows

* Du kanske redan har JDK installerat. Kontrollera detta genom att i ett terminalfönster skriva (observera avslutande c:et):
  ```
  javac --version
  ```
  Om utskriften säger att `javac` saknas eller anger en annan version än version 21, installera då OpenJDK enl. nedan. 

* Installera OpenJDK för ditt system härifrån: [https://adoptium.net/](https://adoptium.net/)
 
    1. Välj att ladda ner OpenJDK **version 21**  (LTS) HotSpot **för ditt operativsystem**. 
    2. Dubbelklicka på filen som laddas ned för att starta installationen. Om du får en varning ska du köra ändå genom att klicka på "Mer information" eller liknande. Under installationen välj alla dessa åtgärder: Update PATH, Associate .jar, Set JAVA_HOME, JavaSoft registry key. 
    3. Starta om din dator.
    4. Starta terminalfönster och kontrollera att `javac --version` ger rätt version. Om något krånglar: fråga någon som installerat JDK förr om hjälp. 

#### Installera Scala med tillhörande verktyg i Windows

* Installera först OpenJDK enligt instruktioner ovan om du inte redan gjort det. 
* Ladda ned filen [`cs-x86_64-pc-win32.zip`](https://github.com/coursier/coursier/releases/latest/download/cs-x86_64-pc-win32.zip) och spara den på valfritt ställe och dubbel-klicka på den när nedladdningen är klar. Följ instruktionerna och svara jakande. Om du får varningar så kör ändå genom att klicka "Mer information" eller liknande.
* Starta om din dator.
* Testa att skriva `scala --version` i ett nytt terminalfönster och om allt gått bra så ska du få en utskrift som börjar med "Scala code runner version 3". Om du får `[warning] MainGenericRunner` skriv `cs install scala:3.7.2` och sedan ska `scala --version` fungera utan varning.
* Installera VS Code med tillägget "Scala (Metals)" enligt instruktioner längre ner under rubriken "EDITOR"
* Installera Kojo enligt instruktioner längre ner under rubriken "KOJO"


### MacOS

#### Terminalfönster i MacOS

* Följ instruktioner här: [https://www.howtogeek.com/682770/how-to-open-the-terminal-on-a-mac/](https://www.howtogeek.com/682770/how-to-open-the-terminal-on-a-mac/)

* Apple har på grund av licensregler bestämt att inte längre inkludera senaste versionen terminalskalet bash i MacOS och har i stället gått över till zsh. Alla instruktioner i pgk och dod förutsätter bash. Följ instruktionerna om hur du får igång bash på MacOS här : [https://www.howtogeek.com/444596/how-to-change-the-default-shell-to-bash-in-macos-catalina/](https://www.howtogeek.com/444596/how-to-change-the-default-shell-to-bash-in-macos-catalina/)
(eller under rubriken *Alternativ terminal på MacOS (Valfri)* senare)

* Det är bra om du installerar en nya version enligt instruktioner här, men det är inte strikt nödvändigt: [https://www.shell-tips.com/mac/upgrade-bash/](https://www.shell-tips.com/mac/upgrade-bash/) 

#### Installera Homebrew (pakethanterare)
Homebrew är det enklaste sättet att installera program och verktyg på macOS. Öppna ett terminalfönster och kör kommando:
```bash
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
```

När installationen är klar, följ de instruktioner som skrivs ut för att lägga till Homebrew i din PATH. Ofta innebär det att köra något i stil med:
```bash
echo 'eval "$(/opt/homebrew/bin/brew shellenv)"' >> ~/.bash_profile
eval "$(/opt/homebrew/bin/brew shellenv)"
```

Kontrollera att Homebrew fungerar genom att köra kommando:
```bash
brew --version
```

#### Alternativ terminal på MacOS (Valfri)
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

#### Installera OpenJDK i MacOS

* Du kanske redan har JDK installerat. Kontrollera detta genom att i ett terminalfönster skriva (observera avslutande c:et):
  ```
  javac --version
  ```
  Om utskriften säger att `javac` saknas eller anger en annan version än version 21, installera då OpenJDK enl. nedan. 

* Installera OpenJDK med hjälp av Homebrew genom kommando:
    ```bash
    brew install openjdk
    ```
* Lägg till JDK i din miljö (så att javac hittas i PATH och JAVA_HOME sätts):
    ```bash
    echo 'export PATH="/opt/homebrew/opt/openjdk@21/bin:$PATH"' >> ~/.bash_profile
    echo 'export JAVA_HOME=$(/usr/libexec/java_home -v 21)' >> ~/.bash_profile
    source ~/.bash_profile
    ```
* Kontrollera att installationen fungerat:
    ```bash
    javac --version
    ```
#### Installera Scala med tillhörande verktyg i MacOS

* Installera först OpenJDK enligt instruktioner ovan om du inte redan gjort det. 
* Installera Scala-verktygen med detta långa terminalkommando på en och samma rad som slutar med `./cs setup`:
    * Installera Scala direkt via Homebrew:
        ```
        brew install scala
        ```
    * Kontrollera att installationen fungerar:
        ```bash
        scala --version
        ```

* Installera VS Code med tillägget "Scala (Metals)" enligt instruktioner längre ner under rubriken "EDITOR"
* Installera Kojo enligt instruktioner längre ner under rubriken "KOJO"


### LINUX

#### Starta terminalfönster i Linux/Ubuntu/WSL

* Tryck Ctrl+Alt+T eller tryck på Windows-tangenten och sök efter "Terminal". 

#### Installera OpenJDK i Linux/Ubuntu/WSL

* Du kanske redan har JDK installerat. Kontrollera detta genom att i ett terminalfönster skriva (observera avslutande c:et):
  ```
  javac --version
  ```
  Om utskriften säger att `javac` saknas eller anger en annan version än version 21, installera då OpenJDK enl. nedan. 

* Om du inte redan har OpenJDK version 21 och du inte hellre använder SDKMAN (se nedan), så öppna terminalfönster och installera med:
```
sudo apt update && sudo apt full-upgrade -y
sudo apt install openjdk-21-jdk openjdk-21-doc openjdk-21-source
```

#### Installera Scala med tillhörande verktyg i Linux/Ubuntu/WSL

* Installera först OpenJDK enligt instruktioner ovan om du inte redan gjort det. 
* Testa om nedladdningsprogrammet `curl` finns på ditt system genom att skriva `curl --version` i terminalen. Om `curl` saknas så installera detta i terminalen genom att skriva: `sudo apt install curl`
* Installera Scala-verktygen med detta långa terminalkommando på en och samma rad som slutar med `./cs setup`:
```
curl -fL https://github.com/coursier/coursier/releases/latest/download/cs-x86_64-pc-linux.gz | gzip -d > cs && chmod +x cs && ./cs setup
```
* Svara med stort Y för ja på eventuell fråga om att addera coursier till din path. 
* Starta om din dator.
* Testa att skriva `scala --version` i ett nytt terminalfönster och om allt gått bra så ska du få en utskrift som börjar med "Scala code runner version 3". Om du får `[warning] MainGenericRunner` skriv `cs install scala:3.7.2` och sedan ska `scala --version` fungera utan varning.
* Installera VS Code med tillägget "Scala (Metals)" enligt instruktioner längre ner under rubriken "EDITOR"
* Installera Kojo enligt instruktioner längre ner under rubriken "KOJO"

### SDKMAN
#### Linux/Ubuntu/WSL: Installera SDKMAN

[https://sdkman.io/](https://sdkman.io/) är ett populärt installationsverktyg för att enkelt installera och hantera olika versioner av allehanda programmeringsverktyg för Ubuntu/Linux/WSL. För dig som hellre vill använda SDKMAN i stället så går det utmärkt att installera Scala-verktugen ovan med hjälp av nedan kommando ett i taget i tur och ornding (om du har [installerat SDKMAN](https://sdkman.io/install)) och svara med stort Y på eventuella frågor om att göra nya versionen default:
```
sdk update
sdk install java 21.0.4-tem
sdk install scala
sdk install scalacli
sdk install sbt
```
*Tips:* Det är viktigt att du noterar HUR du har installerat olika grejer på din dator, speciellt om du blandar olika metoder. Om du behöver uppdatera eller avinstallera så blir det lätt förvirring om du glömt hur du installerat och försöker uppdatera/avinstallera med annan metod än du installerat etc.

### EDITOR
#### Windows/MacOS/Linux/Ubuntu/WSL: Installera VS Code + Metals

1. Installera **VS Code** för ditt system här: [https://code.visualstudio.com/Download](https://code.visualstudio.com/Download) eller genom kommando ```brew install --cask visual-studio-code``` om du installerat Homebrew.
2. Installera tillägget **Scala Metals**. Du kan antingen göra detta via tilläggshanterare (Extensions) inne i VS Code eller via terminalen. 
    - Inifrån VS Code: Tryck Ctrl+Shift+X och skriv i sök-rutan: `Scala Metals`, markera tillägget *Scala (Metals)* och klicka *Install*. 
    - I terminalen skriv: `code --install-extension scalameta.metals --force`
3. Om du kör WSL har du nytta av tillägget "Remote - WSL", sök bland tillägg (Ctrl+Shift+X) och klicka "Install", eller skriv i terminalen  `code --install-extension ms-vscode-remote.remote-wsl  --force`
4. När du ska börja på ett nytt projekt så stäng ned VS Code och förbered VS Code för Scala med följande kommando (välj bättre namn på mittprojekt):
    ```
    mkdir mittprojekt
    cd mittprojekt
    scala setup-ide .
    code .
    ```
    Första gången ett projekt öppnas i VS Code så tar det ett tag innan Metals har byggt allt från grunden. Du kan följa vad som händer i meddelandefältet längst ned; allt är klart när det står "Index complete" efter en raket-ikon.
5. Om VS Code inte fungerar bra med Scala kan det vara bra att stänga ner VS Code och ta bort dessa underkataloger (om de existerar): `.bsp .bloop .vscode .metals .scala-build target` och därefter köra `scala setup-ide .`  innan du startar VS Code igen i aktuell katalog med `code .`

Läs mer om vad du kan göra med en VS Code och andra verktyg i appendix i [kompendiet](https://fileadmin.cs.lth.se/pgk/compendium.pdf/) 

### KOJO
#### Windows/MacOS/Linux/Ubuntu/WSL: Installera Kojo 

Vi använder Kojo på första labben. Kojo är utvecklat speciellt för att hjälpa elever i grundskola och gymnasium att lära sig programmera. 

Det finns olika sätt att köra Kojo:

1. Använd **grafikbiblioteket i kojo** (rekommenderas för vuxna kodare) som fungerar med Scala 3. Ladda ner filen [https://fileadmin.cs.lth.se/kojo.scala](https://fileadmin.cs.lth.se/kojo.scala) och kör enl. instruktioner i kompendiet, t.ex. med `scala repl .` 

2. Kör **Kojo som Desktopapp** (rekommenderas om du har barn i närheten): en nybörjarvänlig utvecklingsmiljö med lättanvänd editor med unga kodare som målgrupp. Använder gamla Scala 2 som kräver klammerparenteser (valfria i Scala 3). Följ installationsinstruktioner för ditt system här: [http://www.kogics.net/kojo-download](http://www.kogics.net/kojo-download)

3. Kör **Kojo i din webbläsare** (rekommenderas bara i nödfall om din dator ej har Scala installerat). Webbversionen har endast en mycket begränsad uppsättning av de kommandon som finns i Kojo Desktop och gamla Scala 2. Skriv och kör din Scala-2-kod direkt här: [http://kojo.lu.se/](http://kojo.lu.se/)

Kojo används på [Vattenhallen Science Center](https://www.vattenhallen.lu.se/upplevelser/programmering/). LTH-studenter med programmeringskunskaper och intresse för pedagogik är välkomna att ansöka om att bli programmeringshandledare i Vattenhallen här: [https://www.vattenhallen.lu.se/om-oss/kontakt/vh-student/student-intresseanmalan/](https://www.vattenhallen.lu.se/om-oss/kontakt/vh-student/student-intresseanmalan/)


## Hårdvara

Vid undervisning på campus rekommenderas LTH:s [Linux-datorer](https://www.lth.se/lthin/datorsalar/vaara-datorsalar/e-huset/). Ta gärna med ett eget tangentbord och en egen mus och koppla in i skolans datorer så minskar du smittrisken. 

När du studerar hemma behöver du en bra arbetsplats och en dator med Linux (t.ex. Ubuntu), Windows eller MacOS. Det är bra att ha en ergonomisk kontorsstol vid ett bekvämt skrivbord, ett flyttbart tangentbord, en separat mus och en höj-och-sänkbar skärm.

### Köpa egen dator

När du ska programmera är det bra med en dator med minst 8GB RAM och minst 1.5GHz CPU.  

Begagnade Lenovo är prisvärda och de brukar vara kontrollerade innan försäljning med garanti från sajter som specialiserar sig på begagnat, tex [Inrego](https://shop.inrego.se/) Prisvärda val är [Thinkpad T490](https://shop.inrego.se/Shop/Product/List?q=T490) eller [Thinkpad T14](https://shop.inrego.se/Shop/Product/List?q=T14) som har matt skärm utan reflexer och bra tangentbord och kostar från ca 3500kr - vill du lägga lite mer så uppgradera till 16GB RAM (+750kr) och SSD 480GB (+1000 kr).

Både [Lenovo](https://www.lenovo.com/se/sv/studentrabatt/) och [Dell](https://www.dell.com/sv-se/shop/dell-advantage/cp/students) har studentrabatt och säljer både stationära och bärbara datorer. Lokala datorbutiker finns här: [Inet i Malmö](https://www.inet.se/), [Webhallen i Malmö](https://www.webhallen.com/), [Compliq i Lund](https://www.compliq.se/bygga-dator)

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
