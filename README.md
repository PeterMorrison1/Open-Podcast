# Open-Podcast

This project is no longer worked on (probably doesn't even run on Android anymore), it is just being left up to see my previous projects.

I learned a lot about podcasts, APIs, and development in general from this. This was before taking the three web development classes and the four programming courses in my degree so its rather rough compared to what I would make today (though I was and still am very proud of it). I want to create a new project that would build on the knowledge gained from this project, university, and my job. I think I could make this exact project faster, better looking, and more functional using React + some node or flask backend on a server + a postgresql database on a server as well.

# Details on Implementation
To explain, the application did not use a server but instead ran entirely on the client by communicating with the itunes podcast api to list, search, and get links to play podcasts. The itunes api was a great learning experience as (at the time, not sure if it changed) it is basically just an index of podcasts that then provides info the user uploaded which is variable qualities of photos, meta data, and a link to their website that has their podcasts in an RSS feed. 

This link to the RSS was then used to find the url to play the media. The system also had a sqlite database to subscribe to podcasts, and other basic podcast features. Using the subscribed list and database the app would then download new episodes as they came out based on the schedule the user set (say it would check at night when plugged in and on wifi).

The api doesn't have any 'top 100' or similar endpoint so I found random highly recommended podcasts and made a main page that showed these and the categories included.


# Images
Subscribed Podcasts List Screen
![Screenshot_20190116-233056_Open Podcast](https://user-images.githubusercontent.com/30943236/143785207-767ae49f-fa30-4891-9b47-64c8a718cd92.jpg)

Episode List / Podcast Info
![Screenshot_20190116-233256_Open Podcast](https://user-images.githubusercontent.com/30943236/143785209-47117857-118d-488e-9d9f-8877139ec72f.jpg)

Play Screen
![Screenshot_20190116-233309_Open Podcast](https://user-images.githubusercontent.com/30943236/143785210-85b175b4-99bb-40b6-9c3c-90f4c866ea4e.jpg)

Search Results
![Screenshot_20190116-233338_Open Podcast](https://user-images.githubusercontent.com/30943236/143785211-66dc4e1b-d773-4e76-98ae-ddfa2112bb08.jpg)

Main Page
![Main Page](https://user-images.githubusercontent.com/30943236/143785269-dae37cb0-255e-4631-9dc1-835469d6ee59.jpg)
