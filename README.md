# son_of_hunkypunk

Fork experimental development of Son of HunkyPunk, see the original project https://github.com/retrobits/son_of_hunkypunk

So far, major changes beyond app version 0.9.0:

1. Android Studio 2.2.2, SDK 24 and related Gradle updates.
2. Git Interpreter upgraded from version 1.2.8 to version 1.3.4 with crash fixed due to long to jlong corruption.
3. Some Git crashes are now trapped. It is still incomplete but less likely to hard-crash the entire app.
4. RecyclerView GameListActivity now available. It is not very pretty and needs visual work, but it is a clean implementation and works with the established backend database.
5. Common code to the old GamesList and new GameListActivity moved to common class outside the Activity.
6. Mutliple file paths are scanned for games. User Interface work not done on pick this yet, only hard coded paths.
