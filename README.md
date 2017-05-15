# ApplaudoHomework
Little app that show infromation about UK soccer teams

Considerations:
This App uses third party libraries for several tasks, although it does not uses the libraries suggested by Applaudo Androd Deveolpment Document, but the ones Im more used to and that I believe are more updated with the latest tendencies in software development (again, this is my opinion)

The project design structure is MVC, so the packages of the project are arranged in MVC terms. Those files that I didnt thought fitted as a view, model or a controler, went to the root folder or the utils package, depending on their functionality.

Testing: The app was tested successfully in 4 phisical devices and 2 emulators: Moto G4 5.5" (Nougat), Moto G2 4.5" (Lolipop), Tablet Ghia Quattro 7" (Marshmallow), LG L500 5" (Lolipop), Emulator Lolipop 7" and Emultator Lolipop 10".

# IMPORTANT:
This project uses google maps api, and it needs an unrestricted api key to work (or restricted to android apps and the package com.sandersoft.applaudohomework). For obvious reasons im not including my private api key, so in order for you to test the correct functionality, you will have to provide your own google maps api key in the file
ApplaudoHomework\app\src\main\res\values\google_maps_api.xml
in the item 'google_maps_key'

Thank you and enjoy.
