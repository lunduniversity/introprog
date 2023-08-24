I denna kurs använder vi programmeringsspråket **Scala** och olika programmeringsverktyg. Läs om hur du använder dessa programmeringsverktyg i **Appendix** i [kompendiet](https://cs.lth.se/pgk/compendium/). Verktygen finns förinstallerade på LTH:s [Linuxdatorer i E-huset](https://www.lth.se/lthin/datorsalar/vaara-datorsalar/e-huset/). Instruktioner om hur du installerar dessa verktyg på din egen dator finns nedan.

## Programmera på LTH:s datorer

På LTH:s [Linuxdatorer i E-huset](https://fileadmin.cs.lth.se/cs/Bilder/Salar/Datorsalar_E-huset.pdf) finns alla de verktyg vi använder i kursen förinstallerade:

* Rekommenderad kodeditor: VS Code startas med kommandot `code .` (Tillägget `Scala Metals` är förinstallerat.)  
* Scala REPL: `scala` eller `scala-cli repl .`
* Utvecklingsmiljön Kojo: `kojo`
* Scala-kompilatorn: `scalac` eller `scala-cli compile .`
* Byggverktyget Scala BUild Tool: `sbt`
* Java-kompilatorn: `javac`
* Exekveringsmijlön JVM med tillhörande utvecklingsbibliotek (OpenJDK): `java`
* Utvecklingsmiljön IntelliJ IDEA med Scala-plugin: `idea`

Mer info [om skolans datorer här](https://www.lth.se/lthin/datorsalar/vaara-datorsalar/e-huset/) och [hur du använder Linux](http://fileadmin.cs.lth.se/cs/Education/EDAA60/general/unix-x.pdf) och [hur du använder bash](https://github.com/RehanSaeed/Bash-Cheat-Sheet).


## Programmera på din egen dator 

Du behöver installera detta på din egen dator: OpenJDK, Scala och VS Code med tillägget Scala (Metals). Följ instruktionerna noga nedan **under rubriken för ditt operativsystem** om hur du installerar verktygen på din egen dator. Du kan läsa mer om hur du använder dessa verktyg i Appendix i [kompendiet]acOS eller Linux/Ubuntu/WSL".
(https://cs.lth.se/pgk/compendium/). 

Många av de programmeringsverktyg vi använder körs via ett terminalfönster. Hur du får igång terminalen beskrivs nedan.

Scala med tillhörande verktyg installeras med hjälp av det officiella installationsverktyget Coursier enligt nedan. Du får då bl.a. följande terminalverktyg som du kan läsa om i kompendiet: `scala`, `scalac`, `scala-cli`, `scaladoc`, `sbt`. 

Om du kör MacOS eller Linux/Ubuntu/WSL så kan du gärna i stället använda den smidiga pakethanteraren SDKMAN, läs mer nedan under rubriken "SDKMAN för dig med MacOS eller Linux/Ubuntu/WSL"

### Windows

#### Terminalfönster i Windows

* För terminal i Windows rekommenderas Microsoft-appen "Windows Terminal", se vidare här: [https://docs.microsoft.com/en-us/windows/terminal/get-started](https://docs.microsoft.com/en-us/windows/terminal/get-started) 
* Du kan också köra den befintliga men antika cmd-terminalen direkt i Windows som inte kräver någon installation, följ instruktioner här [https://www.howtogeek.com/235101/10-ways-to-open-the-command-prompt-in-windows-10/](https://www.howtogeek.com/235101/10-ways-to-open-the-command-prompt-in-windows-10/) och läs mer om vilka kommando som finns i cmd här: [https://ss64.com/nt/](https://ss64.com/nt/)
* Om du har en uppdaterad version av Windows 10 eller 11 och är en van Windows användare så kan du köra **WSL**, helst version 2, som ger dig tillgång till Linux/Ubuntu direkt under Windows i ett separat filsystem. För att installera WSL behöver du starta  **PowerShell** med admin-rättigheter så här: tryck på windows-knappen, skriv `powershell` och högerklicka och välj run as administrator. Följ vidare instruktioner här och välj att installera Ubuntu 22.04 (fråga ngn om hjälp om du kör fast):  [https://docs.microsoft.com/en-us/windows/wsl/install-win10](https://docs.microsoft.com/en-us/windows/wsl/install-win10) När du installerat WSL följ instruktionerna under **Linux-rubrikerna** nedan för att installera kursens programmeringsverktyg under WSL/Ubuntu. 
* Det finns även andra sätt att installera Linux/Ubuntu, t.ex. med [VirtualBox](https://ubuntu.com/tutorials/how-to-run-ubuntu-desktop-on-a-virtual-machine-using-virtualbox#1-overview) (kräver snabb dator och rejät med RAM) eller [Dual Boot](https://medium.com/linuxforeveryone/how-to-install-ubuntu-20-04-and-dual-boot-alongside-windows-10-323a85271a73) (kräver att du har minst 25GB extra utrymme på disken, gärna mer, och att du krymper din Windows-partition och slår på legacy boot i BIOS), eller att du helt [ersätter Windows med Ubuntu](https://ubuntu.com/tutorials/install-ubuntu-desktop) (enklaste sättet och funkar fint även på äldre och klenare datorer). Fråga någon medstudent som vet hur det går till om råd och hjälp om du vill installera Ubuntu "på riktigt". 

#### Installera OpenJDK i Windows

* Du kanske redan har JDK installerat. Kontrollera detta genom att i ett terminalfönster skriva (observera avslutande c:et):
  ```
  javac --version
  ```
  Om utskriften säger att `javac` saknas eller anger en annan version än version 17 eller 11, installera då OpenJDK enl. nedan. 

* Installera OpenJDK för ditt system härifrån: [https://adoptium.net/](https://adoptium.net/)
 
    1. Välj att ladda ner OpenJDK **version 17**  (LTS) HotSpot **för ditt operativsystem**. 
    2. Dubbelklicka på filen som laddas ned för att starta installationen. Om du får en varning ska du köra ändå genom att klicka på "Mer information" eller liknande. Under installationen välj alla dessa åtgärder: Update PATH, Associate .jar, Set JAVA_HOME, JavaSoft registry key. 
    3. Starta om din dator.
    4. Starta terminalfönster och kontrollera att `javac --version` ger rätt version. Om något krånglar: fråga någon som installerat JDK förr om hjälp. 

#### Installera Scala med tillhörande verktyg i Windows

* Installera först OpenJDK enligt instruktioner ovan om du inte redan gjort det. 
* Ladda ned filen [`cs-x86_64-pc-win32.zip`](https://github.com/coursier/coursier/releases/latest/download/cs-x86_64-pc-win32.zip) och spara den på valfritt ställe och dubbel-klicka på den när nedladdningen är klar. Följ instruktionerna och svara jakande. Om du får varningar så kör ändå genom att klicka "Mer information" eller liknande.
* Starta om din dator.
* Testa att skriva `scala --version` i ett nytt terminalfönster och om allt gått bra så ska du få en utskrift som börjar med "Scala code runner version 3".
* Installera VS Code med tillägget "Scala (Metals)" enligt instruktioner längre ner under rubriken "Editor"
* Installera Kojo enligt instruktioner längre ner under rubriken "Kojo"


### MacOS

#### Terminalfönster i MacOS

* Följ instruktioner här: [https://www.howtogeek.com/682770/how-to-open-the-terminal-on-a-mac/](https://www.howtogeek.com/682770/how-to-open-the-terminal-on-a-mac/)

* Apple har på grund av licensregler bestämt att inte längre inkludera senaste versionen terminalskalet bash i MacOS och har i stället gått över till zsh. Alla instruktioner i pgk och dod förutsätter bash. Följ instruktionerna om hur du får igång bash på MacOS här: [https://www.howtogeek.com/444596/how-to-change-the-default-shell-to-bash-in-macos-catalina/](https://www.howtogeek.com/444596/how-to-change-the-default-shell-to-bash-in-macos-catalina/)

* Det är bra om du installerar en nya version enligt instruktioner här, men det är inte strikt nödvändigt: [https://www.shell-tips.com/mac/upgrade-bash/](https://www.shell-tips.com/mac/upgrade-bash/) 

#### Installera OpenJDK i MacOS

* Du kanske redan har JDK installerat. Kontrollera detta genom att i ett terminalfönster skriva (observera avslutande c:et):
  ```
  javac --version
  ```
  Om utskriften säger att `javac` saknas eller anger en annan version än version 17 eller 11, installera då OpenJDK enl. nedan. 

* Installera OpenJDK för ditt system härifrån: [https://adoptium.net/](https://adoptium.net/)
 
    1. Välj att ladda ner OpenJDK **version 17**  (LTS) HotSpot **för ditt operativsystem**. 
    2. Dubbelklicka på filen som laddas ned för att starta installationen. Om du får en varning ska du köra ändå genom att klicka på "Mer information" eller liknande. Under installationen välj alla dessa åtgärder: Update PATH, Associate .jar, Set JAVA_HOME, JavaSoft registry key. 
    3. Starta om din dator.
    4. Starta terminalfönster och kontrollera att `javac --version` ger rätt version. Om något krånglar: fråga någon som installerat JDK förr om hjälp. 

#### Installera Scala med tillhörande verktyg i MacOS

* Installera först OpenJDK enligt instruktioner ovan om du inte redan gjort det. 
* Installera Scala-verktygen med detta långa terminalkommando på en och samma rad som slutar med `./cs setup`:
    * För nya Apple-datorer med arkitekturen "Apple Silicon" (M1, M2, ...):
    ```
    curl -fL https://github.com/VirtusLab/coursier-m1/releases/latest/download/cs-aarch64-apple-darwin.gz | gzip -d > cs && chmod +x cs && (xattr -d com.apple.quarantine cs || true) && ./cs setup
    ```
    * För gamla Apple-datorer med arkitekturen "x86-64":
    ```
    curl -fL https://github.com/coursier/coursier/releases/latest/download/cs-x86_64-apple-darwin.gz | gzip -d > cs && chmod +x cs && (xattr -d com.apple.quarantine cs || true) && ./cs setup
    ```

* Starta om din dator.
* Testa att skriva `scala --version` i ett nytt terminalfönster och om allt gått bra så ska du få en utskrift som börjar med "Scala code runner version 3".
* Installera VS Code med tillägget "Scala (Metals)" enligt instruktioner längre ner under rubriken "Editor"
* Installera Kojo enligt instruktioner längre ner under rubriken "Kojo"


### Linux

#### Starta terminalfönster i Linux/Ubuntu/WSL

* Tryck Ctrl+Alt+T eller tryck på Windows-tangenten och sök efter "Terminal". 

#### Installera OpenJDK i Linux/Ubuntu/WSL

* Du kanske redan har JDK installerat. Kontrollera detta genom att i ett terminalfönster skriva (observera avslutande c:et):
  ```
  javac --version
  ```
  Om utskriften säger att `javac` saknas eller anger en annan version än version 17 eller 11, installera då OpenJDK enl. nedan. 

* Öppna terminalfönster och installera med:
```
sudo apt install openjdk-17-jdk openjdk-17-doc openjdk-17-source
```

#### Installera Scala med tillhörande verktyg i Linux/Ubuntu/WSL

* Installera först OpenJDK enligt instruktioner ovan om du inte redan gjort det. 
* Testa om nedladdningsprogrammet `curl` finns på ditt system genom att skriva `curl --version` i terminalen. Om `curl` saknas så installera detta i terminalen genom att skriva: `sudo apt install curl`
* Installera Scala-verktygen med detta långa terminalkommando på en och samma rad som slutar med `./cs setup`:
```
curl -fL https://github.com/coursier/launchers/raw/master/cs-x86_64-pc-linux.gz | gzip -d > cs && chmod +x cs && ./cs setup
```
* Svara ja med stort Y på eventuell fråga om att addera coursier till din path. 
* Starta om din dator.
* Testa att skriva `scala --version` och `scala-cli --version` i ett nytt terminalfönster och om allt gått bra så ska du få en utskrift som börjar med "Scala code runner version 3".
* Installera VS Code med tillägget "Scala (Metals)" enligt instruktioner längre ner under rubriken "Editor"
* Installera Kojo enligt instruktioner längre ner under rubriken "Kojo"

### SDKMAN
#### SDKMAN för dig med MacOS eller Linux/Ubuntu/WSL

[https://sdkman.io/](https://sdkman.io/) är ett populärt installationsverktyg för att enkelt installera och hantera olika versioner av allehanda programmeringsverktyg för Ubuntu/Linux/WSL/MacOS. För dig som hellre vill använda SDKMAN i stället så går det utmärkt att installera Scala-verktugen ovan med hjälp av nedan kommando ett i taget i tur och ornding (om du har [installerat SDKMAN](https://sdkman.io/install)) och svara med stort Y på eventuella frågor om att göra nya versionen default:
```
sdk update
sdk install java 17.0.8-tem
sdk install scala
sdk install scalacli
sdk install sbt
```
*Tips:* Det är viktigt att du noterar HUR du har installerat olika grejer på din dator, speciellt om du blandar olika metoder. Om du behöver uppdatera eller avinstallera så blir det lätt förvirring om du glömt hur du installerat och försöker uppdatera/avinstallera med annan metod än du installerat etc.

### Editor
#### Installera kod-editorn VS Code och tillägget Scala Metals i Windows/MacOS/Linux/Ubuntu/WSL

1. Installera **VS Code** för ditt system här: [https://code.visualstudio.com/Download](https://code.visualstudio.com/Download) 
2. Installera tillägget **Scala Metals**. Du kan antingen göra detta via tilläggshanterare (Extensions) inne i VS Code eller via terminalen. 
    - Inifrån VS Code: Tryck Ctrl+Shift+X och skriv i sök-rutan: `Scala Metals`, markera tillägget *Scala (Metals)* och klicka *Install*. 
    - I terminalen skriv: `code --install-extension scalameta.metals --force`
3. Om du kör WSL har du nytta av tillägget "Remote - WSL", sök bland tillägg (Ctrl+Shift+X) och klicka "Install", eller skriv i terminalen  `code --install-extension ms-vscode-remote.remote-wsl  --force`
4. Innan du startar VS Code så skapa en tom katalog, stäng VS Code, och navigera till den och förbered VS Code för Scala med följande kommando:
    ```
    mkdir test
    cd test
    scala-cli setup-ide .
    code .
    ```
5. Om VS Code inte fungerar bra med Scala kan det vara bra att stänga ner VS Code och ta bort dessa underkataloger (om de existerar): `.bsp .bloop .vscode .metal .scala-build target` och därefter köra `scala-cli setup-ide .`  innan du startar VS Code igen i aktuell katalog med `code .`

Läs mer om vad du kan göra med en VS Code och andra verktyg i appendix i [kompendiet](https://cs.lth.se/pgk/compendium/) 

### Kojo
#### Installera Kojo i Windows/MacOS/Linux/Ubuntu/WSL

Vi använder Kojo på första labben. Kojo är utvecklat speciellt för att hjälpa elever i grundskola och gymnasium att lära sig programmera. 

Det finns 3 olika sätt att köra Kojo:

1. Använd kodbiblioteket **kojolib** (rekommenderas), som fungerar fint med nya Scala 3. Ladda ner filen [https://fileadmin.cs.lth.se/kojolib.scala](https://fileadmin.cs.lth.se/kojolib.scala) och kör enl. instruktioner i kompendiet, t.ex. med `scala-cli repl .` 

2. Kojo Desktop: en nybörjarvänlig utvecklingsmiljö med lättanvänd editor. Använder gamla Scala 2. Följ installationsinstruktioner för ditt system här: [http://www.kogics.net/kojo-download](http://www.kogics.net/kojo-download)

3. Kör Kojo i din webbläsare. Använder gamla Scala 2 och en begränsad uppsättning av de kommandon som finns i Kojo Desktop. Skriv och kör din kod direkt här: [http://kojo.lu.se/](http://kojo.lu.se/)

Kojo används på [Vattenhallen Science Center](https://www.vattenhallen.lu.se/upplevelser/programmering/). LTH-studenter med programmeringskunskaper och intresse för pedagogik är välkomna att ansöka om att bli programmeringshandledare i Vattenhallen här: [https://www.vattenhallen.lu.se/om-oss/kontakt/vh-student/student-intresseanmalan/](https://www.vattenhallen.lu.se/om-oss/kontakt/vh-student/student-intresseanmalan/)


## Hårdvara

Vid undervisning på campus rekommenderas LTH:s [Linux-datorer](https://www.lth.se/lthin/datorsalar/vaara-datorsalar/e-huset/). Ta gärna med ett eget tangentbord och en egen mus och koppla in i skolans datorer så minskar du smittrisken. 

När du studerar hemma behöver du en bra arbetsplats och en dator med Linux (t.ex. Ubuntu), Windows eller MacOS. Det är bra att ha en ergonomisk kontorsstol vid ett bekvämt skrivbord, ett flyttbart tangentbord, en separat mus och en höj-och-sänkbar skärm.

### Köpa egen dator

När du ska programmera är det bra med en dator med minst 8GB RAM och 2.5GHz CPU. Du kan t.ex. köpa dator här: [Compliq i Lund](https://www.compliq.se/bygga-dator/), [Webbhallen i Malmö](https://www.webhallen.com/se/store/30-Malmo-City), [Inet i Malmö](https://www.inet.se/info/243/inet-malmo)

Både [Lenovo](https://www.lenovo.com/se/sv/studentrabatt/) och [Dell](https://www.dell.com/sv-se/shop/dell-advantage/cp/students) har studentrabatt och säljer både stationära och bärbara datorer.

Om du letar efter en bra stationär dator rekommenderas senaste modellen av [Siago](https://www.atlastsolutions.com/sigao/) som är fläktlös och därmed helt tyst. Sigao kan fås med Ubuntu eller Windows förinstallerat.

### Headset-adapter

Vid distansundervisning är det bra med headset och webbkamera. Det går bra att koppla ditt eget headset till skolans datorer, t.ex. om du behöver prata med en handledare som jobbar hemifrån. Enklast är att använda ett USB-headset då kontakten sitter på framsidan. Om du har ett headset med 4-polig mobil-kontakt medan din dator har två 3-poliga kontakter för mic och hörlurar behövs en sådan här adapter:

<img src="https://github.com/lunduniversity/introprog/raw/master/web/tools/adapter.jpg" alt="drawing" style="width:200px;"/>

Kan t.ex. köpas här: 
[NetOnNet](https://www.netonnet.se/art/ljud-bild/kablar/adapter/andersson-computer-headset-adapter-female/1006756.13721/), 
[Elgiganten](https://www.elgiganten.se/product/ljud-hifi/ljudkablar-adapters/HAMA54572/hama-3-5-mm-adapter-for-headset-med-mikrofon), 
[Webhallen](https://www.webhallen.com/se/product/313979-iiglo-Multimedia-adapter-till-Dator-Svart), 
[Kjell](https://www.kjell.com/se/produkter/dator/horlurar-headset/tillbehor-for-horlurar-headset/datoradapter-for-mobil-headset-p39356), 
[Inet](https://www.inet.se/produkt/8904289/deltaco-adapter-2x3-5mm-ha-till-3-5mm-ho-4-pin-0-1m-svart)
