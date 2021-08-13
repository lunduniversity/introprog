I denna kurs använder vi programmeringsspråket **Scala** och exekveringsmiljön **OpenJDK**, samt byggverktyget `sbt` och editorn `vscode`. Läs om hur du använder dessa och andra programmeringsverktyg i **Appendix** i [kompendiet](https://cs.lth.se/pgk/compendium/). Verktygen finns förinstallerade på LTH:s [Linuxdatorer i E-huset](https://www.lth.se/lthin/datorsalar/vaara-datorsalar/e-huset/). Instruktioner om hur du installerar dessa verktyg på din egen dator finns i Appendix i kompendiet och nedan.

## Programmera på LTH:s datorer

På LTH:s Linux-datorer finns de verktyg vi använder i kursen förinstallerade:

* Rekommenderad kodeditor: VS Code startas med kommandot `code`
* Scala REPL; kommando: `scala`
* Utvecklingsmiljön Kojo; kommando: `kojo`
* Scala-kompilatorn; kommando: `scalac`
* Byggverktyget sbt; kommando: `sbt`
* Java-kompilatorn; kommando: `javac`
* Exekveringsmijlön JVM med tillhörande utvecklingsbibliotek (OpenJDK); kommando: `java`
* Utvecklingsmiljön IntelliJ IDEA med Scala-plugin; kommando: `idea`


## Programmera på din egen dator 

Du behöver följande verktyg på din egen dator: Kojo, OpenJDK, sbt, VS Code med tillägget Scala (Metals). Läs om dessa i Appendix i [kompendiet](https://cs.lth.se/pgk/compendium/) och följ instruktioner nedan om hur du installation verktygen på din egen dator.   


### Installera Kojo

Följ instruktioner här: [http://www.kogics.net/kojo-download](http://www.kogics.net/kojo-download)

Vi använder utvecklingsmiljön Kojo på första labben. Kojo är utvecklat speciellt för att hjälpa elever i grundskola och gymnasium att lära sig programmera. Kojo används på [Vattenhallen Science Center](https://www.vattenhallen.lu.se/upplevelser/programmering/). 

[//]: #(LTH-studenter med programmeringskunskaper och intresse för pedagogik är välkomna att ansöka om att bli programmeringshandledare i Vattenhallen.)


### Starta terminalfönster

Många av de programmeringsverktyg vi använder körs via ett terminalfönster. Du startar ett terminalfönster såhär:

* **Linux/Ubuntu**: Tryck Ctrl+Alt+T eller tryck på Windows-tangenten och sök efter "Terminal". 

* **Windows**: 
    * För Windows rekommenderas Microsoft-appen "Windows Terminal", se vidare här: [https://docs.microsoft.com/en-us/windows/terminal/get-started](https://docs.microsoft.com/en-us/windows/terminal/get-started) 
    * Om du har en uppdaterad version av **Windows 10** och en någorlunda modern dator så rekommenderas **WSL2** som ger dig tillgång till Linux/Ubuntu direkt under Windows, följ instruktioner här och välj Ubuntu 20.04 (fråga ngn om hjälp om du kör fast):  [https://docs.microsoft.com/en-us/windows/wsl/install-win10](https://docs.microsoft.com/en-us/windows/wsl/install-win10)
    * Du kan också köra den befintliga cmd-terminalen som inte kräver någon installation, följ instruktioner här [https://www.howtogeek.com/235101/10-ways-to-open-the-command-prompt-in-windows-10/](https://www.howtogeek.com/235101/10-ways-to-open-the-command-prompt-in-windows-10/) 

* **MacOS**: Följ instruktioner här: [https://www.howtogeek.com/682770/how-to-open-the-terminal-on-a-mac/](https://www.howtogeek.com/682770/how-to-open-the-terminal-on-a-mac/)


## Installera kodeditor

I kurskompendiets appendix finns en beskrivning av vad en kodeditor. Här finns länkar till några populära editorer: 

* [**VS Code**](https://code.visualstudio.com/): rekommenderas i kursen, öppen källkod, alla plattformar. Installera tillägget **Scala Metals** via editorns tilläggshanterare (Extensions Ctrl+Shift+X), skriv i sök-rutan: *Metals*

* [Atom](https://atom.io/): öppen källkod, alla plattformar, paketet Scala Metals finns att installera i editorns tilläggshanterare.

* [Sublime Text](http://www.sublimetext.com/): stängd källkod, alla plattformar, paketet Scala Metals finns att installera i editorns tilläggshanterare.


### Installera Java Development Kit (JDK)

**Kontrollera om du redan har JDK: **
Du kanske redan har JDK installerat. Kontrollera detta genom att i ett terminalfönster skriva (observera avslutande c:et):

```
javac -version
```

Version 11 rekommenderas, men andra versioner kan också fungera. Om utskriften säger att `javac` saknas, installera då OpenJDK enl. nedan.

Du kanske redan har enbart Java Runtime Environment (JRE) installerad, men inte JDK. Då saknar du programmeringsverktygen som ingår i JDK och du behöver installera JDK enl. nedan. Du kan kolla om du har JRE genom att skriva java -version (alltså utan c efter java). Eller så har du redan JDK installerad men inte rätt bibliotek i din PATH; fråga någon om hjälp eller sök information om hur du uppdaterar PATH i ditt operativsystem.

**Installera OpenJDK: **  Läs även instruktionerna i appendix i kompendiet innan du sätter igång.

* Windows/Mac: Installera OpenJDK här [https://adoptopenjdk.net/](https://adoptopenjdk.net/)
 
    1. Välj att ladda ner OpenJDK **version 11**  (LTS) HotSpot **för ditt operativsystem**.
    2. I hämtade filer dubbelklicka för installation med förifyllda val.
    3. Starta om din dator.
    4. Starta terminalfönster och kontrollera enligt ovan att `javac` och `java` ger rätt version.

        * Om något krånglar: fråga någon som installerat JDK förr om hjälp. 

* Linux: Öppna terminalfönster och kör:
    `sudo apt-get install openjdk-11-jdk`



## Installera byggverktyget `sbt`

Installera Scala Build Tool `sbt`: 

* Windows: Ladda ner och kör [https://github.com/sbt/sbt/releases/download/v1.5.5/sbt-1.5.5.msi](https://github.com/sbt/sbt/releases/download/v1.5.5/sbt-1.5.5.msi)

* Linux/Ubuntu/WSL2: Klistra in terminalkommandona rad för rad under rubriken "Linux (deb)" här: [http://www.scala-sbt.org/download.html](http://www.scala-sbt.org/download.html)

* MacOS: Se till att du har admin-rätt på din dator. Installera Homebrew (om du inte redan gjort det) genom att följa instruktionen härifrån http://brew.sh/ dvs kopiera långa kommandot som börjar med /usr/bin/ruby med Cmd+C och klistra in med Cmd+V i terminalen. Skriv sedan detta i terminalen:
```
brew update
brew install sbt@1
```

Läs mer om hur du använder `sbt` i Appendix i [kompendiet](https://cs.lth.se/pgk/compendium/). 

## Gör `scala` och `scalac` tillgängligt i terminalen

Tyvärr finns inget färdigt installationsprogram för nya Scala 3 än, så filerna som kör igång `scala` och `scalac` i terminalen behöver laddas ner och installeras manuellt. Stegen beskrivs nedan. Fråga någon om hjälp hur du gör för att uppdatera din PATH.

* Linux/Ubuntu/WSL/MacOS: 
    * Kopiera och klistra in dessa kommando rad för rad i terminalen:
    ```
    mkdir -p ~/scala && cd ~/scala
    VER=3.0.1
    wget https://github.com/lampepfl/dotty/releases/download/$VER/scala3-$VER.zip
    unzip scala3-$VER.zip
    sudo mkdir -p /usr/local/bin
    sudo ln -s ~/scala/scala3-$VER/bin/sca* /usr/local/bin/.
    rm scala3-$VER.zip
    ```
    * Starta ett **nytt** terminalfönster och testa att skriva `scala -version` och om allt gått bra ska du få en utskrift som börjar med "Scala compiler version".

* MacOS: Gör som för linux men byt ut `wget ` mot `curl -O `  (notera *stora* bokstaven O)
 
* Windows: TODO



## Hårdvara

Vid undervisning på campus rekommenderas LTH:s [Linux-datorer](https://www.lth.se/lthin/datorsalar/vaara-datorsalar/e-huset/). Ta gärna med ett eget tangentbord och en egen mus och koppla in i skolans datorer så minskar du smittrisken. 

När du studerar hemma behöver du en bra arbetsplats och en dator med Linux (t.ex. Ubuntu 20.04), Windows 10 eller macOS. Det är bra att ha en ergonomisk kontorsstol vid ett bekvämt skrivbord, ett flyttbart tangentbord, en separat mus och en höj-och-sänkbar skärm.

### Dator

När du ska programmera är det bra med en dator med minst 8GB RAM och 2.5GHz CPU. Du kan t.ex. köpa dator här: [Compliq i Lund](https://www.compliq.se/bygga-dator/), [Webbhallen i Malmö](https://www.webhallen.com/se/store/22-Malmo-Triangeln), [Inet i Malmö](https://www.inet.se/info/243/inet-malmo)

Både [Lenovo](https://www.lenovo.com/se/sv/studentrabatt/) och [Dell](https://www.dell.com/sv-se/shop/dell-advantage/cp/students) har studentrabatt och säljer både stationära och bärbara datorer. De har även datorer med [Linux förinstallerat](https://news.itsfoss.com/best-linux-laptops-2021/).

Om du letar efter en bra stationär dator rekommenderas senaste modellen av [Siago](https://www.atlastsolutions.com/sigao/) som är fläktlös och därmed helt tyst. Sigao kan fås med Ubuntu eller Windows förinstallerat.

### Headset-adapter

Vid distansundervisning är det bra med headset och webbkamera. Det går bra att koppla ditt eget headset till skolans datorer, t.ex. om du behöver prata med en handledare som jobbar hemifrån. Enklast är att använda ett USB-headset då kontakten sitter på framsidan. Om du har ett headset med 4-polig mobil-kontakt medan din dator har två 3-poliga kontakter för mic och hörlurar behövs en sådan här adapter:

<img src="https://github.com/lunduniversity/introprog/raw/master/web/tools/adapter.jpg" alt="drawing" style="width:200px;"/>

Kan t.ex. köpas här: 
[NetOnNet](https://www.netonnet.se/art/ljud-bild/kablar/adapter/andersson-computer-headset-adapter-female/1006756.13721/), 
[Elgiganten](https://www.elgiganten.se/product/ljud-hifi/ljudkablar-adapters/HAMA54572/hama-3-5-mm-adapter-for-headset-med-mikrofon), 
[Webhallen](https://www.webhallen.com/se/product/313979-iiglo-Multimedia-adapter-till-Dator-Svart), 
[Kjell](https://www.kjell.com/se/produkter/dator/horlurar-headset/tillbehor-for-horlurar-headset/datoradapter-for-mobil-headset-p39356), 
[Mediamarkt](https://www.mediamarkt.se/sv/product/_deltaco-ljudadapter-3-5mm-mikrofon-3-5mm-stereo-hane-till-3-5mm-hona-4-pin-0-1-m-1305626.html), 
[Inet](https://www.inet.se/produkt/8904289/deltaco-adapter-2x3-5mm-ha-till-3-5mm-ho-4-pin-0-1m-svart)
