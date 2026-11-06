# NEXIO HUD v2.1 – Ranked Assistant panel

Telefonra optimalizált, lapozható cheat panel + lebegő HUD a Brawl Stars fölé.
Külön alkalmazás: a játékot nem módosítja, nem olvas a memóriájából, nem automatizál semmit.

## Fül: RANKED ASSISTANT (az egyetlen, egyelőre)
- LOAD: valódi lekérés a Brawlify nyilvános API-járól (brawlerek, pályák, módok, aktív események).
- NINCS még: win rate / matchup adat, rang/ELO, képernyő-felismerés, ban/pick/lane ajánlás.
  A felület ezeket nyíltan "NINCS FORRÁS" / "ADATRA VÁR" állapottal jelzi, ajánlást nem talál ki.

## Betűtípus (Lilita One)
Töltsd le a Google Fontsról, és tedd `app/src/main/res/font/lilita_one.ttf` néven a projektbe.
Nélküle az app működik, csak tartalék betűvel.

## APK GitHubon
Új repó → a zip tartalma vagy az egész mappa feltöltve → a repó gyökerében `.github/workflows/build.yml`
(a zipben benne van) → Actions → Build APK → Run workflow → Artifacts: `nexio-hud-v2-1-debug-apk`.
FONTOS: régi repóba ne tölts az új fölé, mert a törölt régi fájlok bennmaradnak és hibát okoznak.

## Hibakeresés
- HUD nem jelenik meg: Beállítások (⚙) → engedély; HUD kapcsoló ki-be.
- LOAD hibát ír: internet nélkül, vagy ha a Brawlify API válasza más alakú. A hibaüzenetet küldd el.
- Elcsúszott panel: ⚙ → Pozíció visszaállítása.
