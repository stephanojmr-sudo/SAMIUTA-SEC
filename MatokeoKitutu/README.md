# Matokeo Kitutu — Android Native App

Programu asilia ya Android (Kotlin + Jetpack Compose + Room + DataStore) kwa
usimamizi wa matokeo ya wanafunzi wa Kidato cha Pili, Kata ya Kitutu
(Samamba SS, Utaho SS, Miandi SS).

## Muundo wa mradi

```
app/src/main/java/com/kitutu/matokeo/
  data/       -> Room (StudentEntity, ArchiveEntity, DAOs), DataStore (SettingsRepository),
                 AppRepository (inayounganisha vyote), Models.kt (vigezo na hesabu za daraja/GPA)
  ui/
    AppViewModel.kt   -> hali (state) na vitendo vyote vya programu
    NavGraph.kt       -> uongozaji kati ya skrini
    theme/            -> rangi na mandhari (Material3)
    screens/
      HomeScreen.kt         -> Mwalimu / Mwanafunzi / Admin
      TeacherScreens.kt     -> chagua shule, dashibodi (Wanafunzi + Jaza Matokeo), fomu ya usajili
      StudentPortalScreen.kt-> tazama matokeo (Yote + kila somo)
      AdminScreens.kt       -> login, dashibodi (lock/unlock, password, hifadhi za mwezi)
```

Data zote (wanafunzi, alama, hifadhi za mwezi) zinahifadhiwa **kwenye kifaa
chenyewe** kwa Room (SQLite) na Jetpack DataStore — hazihitaji intaneti.

## Vipengele vilivyomo (Awamu ya 1)

- Mwalimu: chagua shule → password (inalazimika kubadilishwa mara ya kwanza) →
  andikisha wanafunzi (majina matatu + jinsia + namba ya mtihani) → jaza alama
  somo kwa somo
- Mwanafunzi: tazama "Matokeo Yote" na kila somo, yakiwa yamepangwa kwa nafasi
- Admin: login, funga/ruhusu uingizaji wa alama, badilisha password (yake na
  za walimu), weka kichwa cha ripoti (mwezi), hifadhi matokeo ya mwezi, tazama/
  futa kabisa/rejesha hifadhi, futa alama zote (majina yanabaki)

## Vipengele vya Awamu ya 2 (bado havijajengwa humu)

Toleo la wavuti (HTML) lililotangulia lilikuwa na vipengele vya ziada
ambavyo havijahamishiwa humu kwa sababu ya ukubwa wa kazi (Compose/Kotlin
haina zana za moja kwa moja za "print" au CSV kama kivinjari):

- Kupakua/kupakia CSV (template ya matokeo na ya usajili wa wanafunzi)
- Kuchapisha (print) ripoti kama PDF
- Takwimu za kina (Idadi ya Madaraja, GPA ya shule, 20 Bora/Duni wenye maelezo)
- Tab ya "Yaliyohifadhiwa" upande wa mwalimu

Haya yanaweza kuongezwa baadaye — muundo wa msingi (Room + Repository +
ViewModel) tayari umewekwa vizuri ili kukubali maboresho hayo bila kuvunja
kilichopo.

## Jinsi ya kuufungua

1. Sakinisha [Android Studio](https://developer.android.com/studio) (toleo
   la hivi karibuni — Hedgehog au jipya zaidi).
2. Fungua folda hii (`MatokeoKitutu`) kama mradi ("Open an existing project").
3. Acha Android Studio i-download Gradle na dependencies (inahitaji intaneti
   mara ya kwanza tu).
4. Bonyeza **Run ▶** kwenye kifaa halisi au emulator (Android 7.0 / API 24
   na zaidi).

> **Kumbuka:** Mradi huu umeandikwa kwa uangalifu lakini **haujajengwa
> (compile) wala kujaribiwa** kwenye Android Studio halisi kwa sababu
> mazingira niliyotumia kuutengeneza hayana Android SDK. Kuna uwezekano
> mdogo wa kukutana na hitilafu ndogo za awali za ujenzi (mfano toleo la
> Gradle/Kotlin kutolingana na Android Studio yako) — kawaida hurekebishwa
> kwa "Sync Project with Gradle Files" na kukubali masasisho
> yanayopendekezwa na Android Studio.

## Jinsi ya kuipandisha GitHub

Mazingira niliyotumia kuandaa mradi huu hayana muunganiko wa intaneti, kwa
hiyo sikuweza ku-*push* moja kwa moja. Fanya haya kwenye kompyuta yako
(ndani ya terminal, kwenye folda hii `MatokeoKitutu`):

```bash
git init
git add .
git commit -m "Awamu ya 1: Matokeo Kitutu - Android native app"
```

Kisha:

1. Nenda [github.com/new](https://github.com/new) na tengeneza repository
   mpya (mfano jina: `matokeo-kitutu-android`). **Usiweke** README/.gitignore
   kwenye GitHub (tayari tumeviweka hapa) ili kuepuka mgongano.
2. GitHub itakupa amri za kuunganisha — kwa kawaida ni kama hizi (badilisha
   `JINA-LAKO` na jina la account yako):

```bash
git branch -M main
git remote add origin https://github.com/JINA-LAKO/matokeo-kitutu-android.git
git push -u origin main
```

3. Onyesha upya ukurasa wa GitHub — faili zote zitaonekana pale.

Baada ya hapo, mabadiliko yoyote ya baadaye ni:

```bash
git add .
git commit -m "Maelezo ya mabadiliko"
git push
```
