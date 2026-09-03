# Instruktioner

I denna kurs använder vi programmeringsspråket **Scala** med olika programmeringsverktyg.  
Läs om hur du använder verktygen i **Appendix** i [kompendiet](https://fileadmin.cs.lth.se/pgk/compendium.pdf).  

Verktygen finns förinstallerade på LTH:s [Linuxdatorer i E-huset](https://fileadmin.cs.lth.se/cs/Bilder/Salar/Datorsalar_E-huset.pdf), se även [Programmera på LTH:s datorer](#programmera-på-lths-datorer).

Tips om köp av egen dator finns under [Hårdvara](#hårdvara).

---

## Programmera på din egen dator

Du behöver installera:

- **OpenJDK 25**
- **Scala 3**
- **VS Code** med tillägget **Scala (Metals)**
- **Git**

Följ instruktionerna noga under rubriken för ditt operativsystem.

### Välj operativsystem/metod

- [Windows](#windows)
- [macOS](#macos)
- [Linux (Ubuntu)](#linux-ubuntu)
- [WSL - Windows Subsystem for Linux](#wsl)
- [Dual booting](#dual-booting)
- [SDKMAN (Linux/macOS/WSL)](#sdkman-valfritt)
- [Hårdvara](#hårdvara)

Om problem uppstår kan du få installationshjälp under kursens första två veckor på luncherna i E:2116. Dessutom kommer du ha många duktiga kurskamrater som har erfarenhet av datorer. Passa på att fråga de för hjälp och på så viss skapa nya vänner.

---

## Windows

Innan du börjar:

- Innan du installerar kontrollera om datorn är **x86** eller **ARM** (vanligast är x86):  
  [Settings -> System -> About](ms-settings:about)  
  [Microsoft-guide System](https://support.microsoft.com/en-US/Windows/Experience/find-information-about-your-windows-device)
  Kom ihåg arkitekturen för senare.

- Slå på visning av filändelser och dolda filer i Utforskaren:  
  [Microsoft-guide Explorer](https://support.microsoft.com/en-US/Windows/Experience/FileExplorer/file-explorer-in-windows)

### 1) Terminal

- Rekommenderat: **Windows Terminal** (standard och förinstallerat i Windows 11).  
  Installationsguide: <https://learn.microsoft.com/en-us/windows/terminal/install>
- Kommandoreferenser:
  - CMD: <https://ss64.com/nt/>
  - PowerShell (Windows Terminal): <https://ss64.com/ps/>
- Om du föredrar Unix-lik terminal: använd **Git Bash** (installeras med Git senare).

### 2) Installera OpenJDK 25

Kontrollera först i terminalen (glöm inte sista c:et i javac):

```bash
javac --version
```
- Det går bra med Java JDK 21 också.
- Om `javac` saknas eller versionen är något annat än **25** eller **21**: installera enligt instruktionerna nedan.
- Om du har en annan JDK: avinstallera den först.

Installera från: <https://adoptium.net/>

1. Välj **Download Temurin**.
2. Kör installationsfilen.
>Om du får en varning ska du köra ändå genom att klicka på "Mer information" eller liknande, och `run anyway`. 
3. **VIKTIGT:** Välj **Install Entire Feature** för:
   - Modify PATH variable
   - Associate .jar
   - Set or override JAVA_HOME variable
   - JavaSoft (Oracle) registry keys
4. Starta om datorn.
5. Verifiera igen med (notera c:et i javac):

```bash
javac --version
```

### 3) Installera Scala

1. Välj metod beroende på arkitektur:

- **x86**:  
  Ladda ner  
  <https://github.com/coursier/coursier/releases/latest/download/cs-x86_64-pc-win32.zip>  
  Packa upp (extract) och kör installationsfilen.

- **ARM**: 
 Öppna terminalen, kör kommandon ett i taget:

```powershell
Start-BitsTransfer -Source https://github.com/coursier/launchers/raw/master/coursier -Destination coursier
Start-BitsTransfer -Source https://github.com/coursier/launchers/raw/master/coursier.bat -Destination coursier.bat
.\coursier setup
```

- **ARM** (endast CMD), kör kommandon ett i taget:

```cmd
bitsadmin /transfer downloadCoursierCli https://github.com/coursier/launchers/raw/master/coursier "%cd%\coursier"
bitsadmin /transfer downloadCoursierBat https://github.com/coursier/launchers/raw/master/coursier.bat "%cd%\coursier.bat"
.\coursier setup
```

2. Starta om datorn.
3. Kontrollera:

```bash
scala --version
```

Du ska få något som börjar med `Scala code runner version 3`.

Om du får varning med `MainGenericRunner`, installera explicit Scala-version:

```bash
cs install scala:3.8.4
```

### 4) Installera VS Code + Scala (Metals)

1. Installera VS Code: <https://code.visualstudio.com/Download>
2. Installera tillägget **Scala (Metals)** via Extensions(bilden med 4 lådor till vänster), eller klistra i terminalen:

```bash
code --install-extension scalameta.metals --force
```

3. Första gången ett projekt öppnas i VS Code kan indexering ta tid. Du kan följa vad som händer i meddelandefältet längst ned; allt är klart när status visar **Index complete**.

4. Vid problem: stäng VS Code och ta bort ev. kataloger:
   `.bsp`, `.bloop`, `.vscode`, `.metals`, `.scala-build`, `target`

5. Starta om VS Code.

### 5) Installera Kojo
Vi använder Kojo på första labben. Kojo är utvecklat speciellt för att hjälpa elever i grundskola och gymnasium att lära sig programmera.
1. Skapa en mapp och öppna den i VS Code genom att följa `File -> Open Folder... -> Hitta och tryck på mappen ->  Open`
> OBS! Öppna inte filen direkt eller en mapp som innehåller många filer (t.ex Downloads), skapa alltid en ny mapp först och använd den.
2. Ladda ner filen till mappen: <https://fileadmin.cs.lth.se/kojo.scala>
3. Öppna terminalen i VS Code genom att följa `Terminal -> New Terminal` och skriv i den

```bash
scala repl .
```
4. Skriv i den nya terminalen som ser som nedan kommandot `fram`
```bash 
scala>
```
5. Om en fönster öppnar och en padda dyker upp har du installerat den korrekt.

Kojo används på [Vattenhallen Science Center](https://www.vattenhallen.lu.se/upplevelser/programmering/). LTH-studenter med programmeringskunskaper och intresse för pedagogik är välkomna att ansöka om att bli programmeringshandledare i Vattenhallen här: [https://www.vattenhallen.lu.se/om-oss/kontakt/vh-student/student-intresseanmalan/](https://www.vattenhallen.lu.se/om-oss/kontakt/vh-student/student-intresseanmalan/)

*Valfritt*: Installera skrivbordsappen Kojo här: https://www.kogics.net/kojo-download där du kan programmera i äldre Scala 2 (se Appendix 1).

### 6) Installera Git

1. Ladda ner: <https://git-scm.com/download/win>  
   Välj:
   - **Windows/x64 Setup** för x86
   - **Windows/ARM64 Setup** för ARM
2. Följ standardvalen (editor: gärna **nano**).
3. Verifiera:

```bash
git --version
```

### 7) Ladda ner Workspace
För att kunna uppföra labbarna i denna kurs behöver du en workspace med redan givna filer.
1. Ladda ner och packa upp <https://fileadmin.cs.lth.se/pgk/workspace.zip>
2. Öppna mappen i VS Code genom att följa `File -> Open Folder... -> Hitta och tryck på mappen ->  Open`
3. Utför dina labbar i respektive mapp för varje labb.

---

## macOS

### 1) Innan du börjar:

1. Öppna terminalen, se guide här: <https://www.howtogeek.com/682770/how-to-open-the-terminal-on-a-mac/>

2. Kontrollera arkitektur, **x86** eller **ARM** genom att skriva i terminalen:

```bash
uname -m
```
Kom ihåg arkitekturen till senare.

3. Slå på visning av filändelser och dolda filer, också i terminalen:

```bash
defaults write NSGlobalDomain AppleShowAllExtensions -bool true && killall Finder
defaults write com.apple.finder AppleShowAllFiles YES && killall Finder
```


> macOS använder ofta `zsh` som standard. Det fungerar bra. Bash är valfritt.

### 2) Installera Homebrew
Installera Homebrew genom att klistra:
```bash
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
```
Lägg till brew i PATH, klistra en beroende på arkitektur:
**ARM:**
```bash
echo 'eval "$(/opt/homebrew/bin/brew shellenv)"' >> ~/.zprofile
eval "$(/opt/homebrew/bin/brew shellenv)"
```

**x86:**
```bash
echo 'eval "$(/usr/local/bin/brew shellenv)"' >> ~/.zprofile
eval "$(/usr/local/bin/brew shellenv)"
```

Verifiera att det blev installerat:

```bash
brew --version
```

#### **Valfritt**: iTerm2 + Bash
MacOS kommer med en inbyggd terminal, men många föredrar att använda iTerm2 som är mer flexibel.
Installera iTerm2 (valfritt) och Bash med:
```bash
brew install --cask iterm2
brew install bash
```

Nu kan du byta till bash:
ARM:

```bash
chsh -s /opt/homebrew/bin/bash
```

x86:
```bash
chsh -s /usr/local/bin/bash
```
Du kan nu stänga terminalen och öppna iTerm2.

### 3) Installera OpenJDK 25

Kontrollera först om du har redan java:

```bash
javac --version
```

Om `javac` saknas eller versionen < 25:

```bash
brew install openjdk@25
```

Lägg till i miljön:

```bash
echo 'export PATH="$(brew --prefix openjdk@25)/bin:$PATH"' >> ~/.zprofile
echo 'export JAVA_HOME=$(brew --prefix openjdk@25)' >> ~/.zprofile
source ~/.zprofile
```

Kolla vad som nu ligger i ~/.zprofile och PATH och JAVA_HOME:
```bash
cat ~/.zprofile
echo $PATH
echo $JAVA_HOME
```
JAVA_HOME ska inte vara tom och $PATH ska innehålla något med openjdk.

Verifiera att det blev installerat:

```bash
javac -version
java -version
```

### 4) Installera Scala och Scala CLI

```bash
brew install scala
```

Verifiera att det blev installerat:
```bash
scala --version
```

### 5) Installera VS Code + Scala (Metals)

1. Ladda ner: <https://code.visualstudio.com/Download>  
   eller:

```bash
brew install --cask visual-studio-code
```

2. Installera tillägget **Scala (Metals)** via Extensions(bilden med 4 lådor till vänster), eller klistra i terminalen:
```bash
code --install-extension scalameta.metals --force
```

3. Första gången ett projekt öppnas i VS Code kan indexering ta tid. Du kan följa vad som händer i meddelandefältet längst ned; allt är klart när status visar **Index complete**.

4. Vid problem: stäng VS Code och ta bort ev. kataloger:
   `.bsp`, `.bloop`, `.vscode`, `.metals`, `.scala-build`, `target`

5. Starta om VS Code.

### 6) Installera Kojo
Vi använder Kojo på första labben. Kojo är utvecklat speciellt för att hjälpa elever i grundskola och gymnasium att lära sig programmera.
1. Skapa en mapp och öppna den i VS Code genom att följa `File -> Open Folder... -> Hitta och tryck på mappen ->  Open`
> OBS! Öppna inte Downloads eller Documents direkt, alltid skapa en ny mapp först och använd den.
2. Ladda ner filen till mappen: <https://fileadmin.cs.lth.se/kojo.scala>
3. Öppna terminalen i VS Code genom att följa `Terminal -> New Terminal` och skriv i den

```bash
scala repl .
```
4. Skriv i den nya terminalen som ser som nedan kommandot `fram`
```bash 
scala>
```
5. Om en fönster öppnar och en padda dyker upp har du installerat den korrekt.

*Valfritt*: Installera skrivbordsappen Kojo här: https://www.kogics.net/kojo-download där du kan programmera i äldre Scala 2 (se Appendix 1).


### 7) Installera Git

Klistra i terminalen:
```bash
brew install git
git --version
```

### 8) Ladda ner Workspace
För att kunna uppföra labbarna i denna kurs behöver du en workspace med redan givna filer.
1. Ladda ner och packa upp <https://fileadmin.cs.lth.se/pgk/workspace.zip>
2. Öppna mappen i VS Code genom att följa `File -> Open Folder... -> Hitta och tryck på mappen ->  Open`
3. Utför dina labbar i respektive mapp för varje labb.
---

## Linux (Ubuntu)

Innan du börjar:

1. Kontrollera arkitektur:

```bash
uname -m
```
Kom ihåg arkitekturen för senare.

2. Slå på visning av dolda filer och filtyp:

```bash
gsettings set org.gtk.gtk4.Settings.FileChooser show-hidden true
gsettings set org.gtk.gtk4.Settings.FileChooser show-type-column true
```

### 1) Öppna terminal

Tryck `Ctrl + Alt + T`

### 2) Installera OpenJDK 25

Kontrollera om det är redan installerat:

```bash
javac --version
```

Om det saknas eller versionen är äldre an 25, uppdatera installationen:

```bash
sudo apt update && sudo apt full-upgrade -y
```

Sedan installera Java 25:
```bash
sudo apt install openjdk-25-jdk openjdk-25-doc openjdk-25-source
```

Verifiera:

```bash
javac --version
```

### 3) Installera Scala-verktyg

Kontrollera om `curl` finns:

```bash
curl --version
```

Installera vid behov:

```bash
sudo apt install curl
```

Installera med Coursier:

- **x86**:

```bash
curl -fL https://github.com/coursier/coursier/releases/latest/download/cs-x86_64-pc-linux.gz | gzip -d > cs && chmod +x cs && ./cs setup
```

- **ARM64**:

```bash
curl -fL https://github.com/VirtusLab/coursier-m1/releases/latest/download/cs-aarch64-pc-linux.gz | gzip -d > cs && chmod +x cs && ./cs setup
```

Svara `Y` om du blir tillfrågad om PATH. Starta om datorn och kontrollera:

```bash
scala --version
```

Vid `MainGenericRunner`-varning:

```bash
cs install scala:3.8.4
```

### 4) Installera VS Code + Scala (Metals)

1. Ladda ner: <https://code.visualstudio.com/Download>
2. Installera Metals:

```bash
code --install-extension scalameta.metals --force
```

3. Vänta tills **Index complete**.
4. Vid problem, rensa `.bsp`, `.bloop`, `.vscode`, `.metals`, `.scala-build`, `target`.

### 5) Installera Kojo
Vi använder Kojo på första labben. Kojo är utvecklat speciellt för att hjälpa elever i grundskola och gymnasium att lära sig programmera.
1. Skapa en mapp och öppna den i VS Code genom att följa `File -> Open Folder... -> Hitta och tryck på mappen ->  Open`
> OBS! Öppna inte Downloads eller Documents direkt, alltid skapa en ny mapp först och använd den.
2. Ladda ner filen till mappen: <https://fileadmin.cs.lth.se/kojo.scala>
3. Öppna terminalen i VS Code genom att följa `Terminal -> New Terminal` och klistra i den

```bash
scala repl .
```
4. Skriv i den nya terminalen som ser som nedan kommandot `fram`
```bash 
scala>
```
5. Om en fönster öppnar och en padda dyker upp har du installerat den korrekt.

*Valfritt*: Installera skrivbordsappen Kojo här: https://www.kogics.net/kojo-download där du kan programmera i äldre Scala 2 (se Appendix 1).

### 6) Installera Git

Klistra i terminalen och kolla att det blev installerat:
```bash
apt-get install git
git --version
```

### 7) Ladda ner Workspace
För att kunna uppföra labbarna i denna kurs behöver du en workspace med redan givna filer.
1. Ladda ner och packa upp <https://fileadmin.cs.lth.se/pgk/workspace.zip>
2. Öppna mappen i VS Code genom att följa `File -> Open Folder... -> Hitta och tryck på mappen -> Open`
3. Utför dina labbar i respektive mapp för varje labb.

---

## WSL

**WSL2 rekommenderas** om du vill ha Linuxmiljö men behålla Windows.

1. Öppna **Windows Terminal som administratör** och följ:  
   <https://docs.microsoft.com/en-us/windows/wsl/install>
2. När Ubuntu är installerat, följ avsnittet [Linux (Ubuntu)](#linux-ubuntu) genom samma terminal. Du kan alltid öppna terminalen genom att söka Ubuntu i Windows.
3. Installera VS Code i [Windows](#windows) + tillägget **Remote - WSL**:  
   <https://code.visualstudio.com/docs/remote/wsl>
4. Ljud i WSL2 (vid behov):  
   <https://github.com/lunduniversity/introprog/blob/master/web/tools/sound_in_wsl2.md>

---

## Dual booting

Du kan:

- ersätta Windows med Ubuntu: <https://ubuntu.com/tutorials/install-ubuntu-desktop>
- eller köra dual boot:  
  <https://linuxblog.io/dual-boot-linux-windows-install-guide/>

---

## SDKMAN (Valfritt)

För Linux/macOS/WSL kan du använda istället [SDKMAN](https://sdkman.io/) för versionshantering.

Installera (efter att SDKMAN installerats enligt deras guide):

```bash
sdk update
sdk install java 25
sdk install scala
sdk install scalacli
sdk install sbt
```

Svara `Y` om du får fråga om att sätta version som default.

> **Tips:** Blanda inte installationsmetoder i onödan (brew/apt/sdkman/coursier), det försvårar uppdatering och felsökning.

---

## Programmera på LTH:s datorer

På LTH:s [Linuxdatorer i E-huset](https://fileadmin.cs.lth.se/cs/Bilder/Salar/Datorsalar_E-huset.pdf) finns verktygen förinstallerade:

- VS Code: `code .` (Metals förinstallerat)
- Scala REPL: `scala repl .`
- Scala-kompilator: `scala compile .`
- Kompilera/kör Scala: `scala .`
- Kojo: `kojo`
- SBT: `sbt`
- Java-kompilator: `javac`
- JVM/OpenJDK: `java`

För att kunna programmera på LTHs datorer och komma in i datorsalarna måste du skaffa ett LU-kort. Detta brukar göras i samband med nollningen, men om du saknar en läs mer här: <https://www.lu.se/student/it-tjanster-och-studentsupport/lu-kortet> 
Om du kan inte logga in på LTHs datorer, du behöver återställa din lösenord, följ: <https://www.student.lth.se/stoed-och-service/it-tjaenster-och-support/faq-fraagor-och-svar/>

Mer info:

- [LTH datorsalar](https://www.lth.se/lthin/datorsalar/vaara-datorsalar/e-huset/)
- [Linux-intro (PDF)](http://fileadmin.cs.lth.se/cs/Education/EDAA60/general/unix-x.pdf)
- [Bash cheat sheet](https://github.com/RehanSaeed/Bash-Cheat-Sheet)

---

## Hårdvara

Vid undervisning på campus rekommenderas LTH:s [Linux-datorer](https://www.lth.se/lthin/datorsalar/vaara-datorsalar/e-huset/).

Hemma rekommenderas en bra arbetsplats samt dator med Linux, Windows eller macOS.

### Köpa egen dator

För programmering rekommenderas minst:

- **16 GB RAM**
- modern fler-kärnig CPU (t.ex. Ryzen eller Intel Core i5/i7/Ultra)

Lenovo ThinkPad nämns ofta som robust alternativ. Begagnat kan vara prisvärt:

- [Inrego](https://shop.inrego.se/) (t.ex. ThinkPad T14)
- [Inet Refurbished](https://www.inet.se/kategori/1323/refurbished-begagnad/521/lenovo)
- [BilligTeknik](https://www.billigteknik.se/985-lenovo-thinkpad)

Nya datorer med studentrabatt:

- [Lenovo studentrabatt](https://www.lenovo.com/se/sv/studentrabatt/)
- [Dell studentrabatt](https://www.dell.com/sv-se/shop/dell-advantage/cp/students)

Lokala butiker:

- [Inet Malmö](https://www.inet.se/)
- [Webhallen Malmö](https://www.webhallen.com/)
- [Compliq Lund](https://www.compliq.se/bygga-dator)

Fläktlös/tyst stationär dator:

- [AtLast Solutions](https://www.atlastsolutions.com/)

### Headset-adapter

Om du har headset med 4-polig mobilkontakt men dator med separata 3-poliga in/utgångar behövs adapter.

![Headset-adapter](https://github.com/lunduniversity/introprog/raw/master/web/tools/adapter.jpg)

Exempel på butiker:

- [NetOnNet](https://www.netonnet.se/art/ljud-bild/kablar/adapter/andersson-computer-headset-adapter-female/1006756.13721/)
- [Elgiganten](https://www.elgiganten.se/product/ljud-hifi/ljudkablar-adapters/HAMA54572/hama-3-5-mm-adapter-for-headset-med-mikrofon)
- [Webhallen](https://www.webhallen.com/se/product/313979-iiglo-Multimedia-adapter-till-Dator-Svart)
- [Kjell](https://www.kjell.com/se/produkter/dator/horlurar-headset/tillbehor-for-horlurar-headset/datoradapter-for-mobil-headset-p39356)
- [Inet](https://www.inet.se/produkt/8904289/deltaco-adapter-2x3-5mm-ha-till-3-5mm-ho-4-pin-0-1m-svart)
