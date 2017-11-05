# Assignment 2 - Quote List Application with Firebase integration

This Android application (QuotesPeterLyrene) displays various quotes related to food. 

Data is stored in an cloud-hosted NoSQL database (Firebase). The database stores quote information among five categories, with four quotes per category.

From the main Activity, users can tap on any category to view short versions of the quotes in that category. Tapping on a short quote launches the QuoteActivity, displaying all information about the quote. Tapping the name under "Attributed" will bring up a small blurb about the quote's original speaker. Tapping the "Reference" link at the bottom of the page will launch a web browser and open the web page the quote was originally retrieved from.

On any Activity, the options menu can be accessed. The options menu has three possible choices:

* About - Launches the AboutActivity, displaying information to the user about how the app works & who developed it.
* Random quote - Launches QuoteActivity, displaying a random quote.
* Last viewed quote - Launches QuoteActivity, displaying the last quote the user has viewed. If the user has not yet viewed a quote, they will see a message saying so.

Created by: Lyrene Labor, Peter Bellefleur
