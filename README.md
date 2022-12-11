# imCHEAP
Finding you the best prices around the world!

![image](https://user-images.githubusercontent.com/77209542/206880879-2348276c-3afb-40c6-84ac-07e31776f06d.png)
![image](https://user-images.githubusercontent.com/77209542/206881308-dd1a35be-509e-472a-aec5-8ad6fd11aa0c.png)






With the cheekily-named imCHEAP project, our team set out to design a fun and user-friendly Android app for the backpacker, the hostel-to-hostel traveler, and the relaxed browser, all in search of the cities with the cheapest prices and best forecasted weather. As a team of 4, we worked in a fast-paced and agile framework, made use of a Trello board for weekly sprint management, and facilitated our collaboration via GitHub.

# Project Overview
We’ve relied on the following tech stack, tools and APIs in order to bring this MVP to life:
Android Studio
GitHub
Firebase
Trello
APIs
https://cost-of-living-and-prices.p.rapidapi.com/prices

https://cost-of-living-and-prices.p.rapidapi.com/cities

https://countryflagsapi.com/png/

https://openweathermap.org/api 


# Data Requirements
![data req](https://user-images.githubusercontent.com/77209542/206880982-f1c4a99f-c922-47ef-b409-9f7bb6703b66.png)

# Planning and Documentation
![image](https://user-images.githubusercontent.com/77209542/206881370-74e7f916-8e3f-42f8-9eb1-72298a912a2a.png)


# Demo
Login page:

https://user-images.githubusercontent.com/77209542/206880833-173bab34-0f9c-4eff-adf5-76c224daf7c2.mp4

Search:

https://user-images.githubusercontent.com/77209542/206880834-e2ae043b-8402-41b4-9a7e-30a7440324e0.mp4


City summary and weather:

https://user-images.githubusercontent.com/77209542/206880835-dd186576-bbd7-4a4f-bb40-39eeadb17f31.mp4


Profile and favourites:

https://user-images.githubusercontent.com/77209542/206880836-94a806d8-7830-4e54-9e9d-a9f258624ce0.mp4


# Open Issues

Searching can sometimes be slow when polling the API. Upon investigation, it is because of polling API limitations. Searching too many times in rapid succession causes a timeout. This can be solved by purchasing an API service with better limits on querying.
The flag API also sometimes goes down. Upon investigation, it is because of regular maintenance. A dummy flag has been implemented to show when the flag API call does not return a flag.
In addition, Firebase also has a limited timeframe for client access. The free student account would have to be upgraded to a subscription for ongoing access.

# Risks
Because we use free APIs in our project, they are subject to instability and termination of service. This issue can be solved by purchasing a subscription to a more reliable API service.
API keys are also exposed in the source code. The code base is set as private, but there is still a chance that it can be leaked.
