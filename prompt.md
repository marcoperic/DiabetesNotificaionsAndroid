I am looking to implement a Diabetes Notification app that allows for users to get notifications daily reminding them to take care of their diabetes.

As such, the app will serve as the settings for the user to configure notification frequency and content.

Please implement the following:

- Robust notification system. It should work when the app is not running or when the device is restarted. Notifications should appear as banners, make a notification sound effect, and appear on the lock screen.

- Basic scheduling. In the app, the user should be able to set notifications to come at a certain time every day, specify a window for notifications to appear (for example, a 2-hour window from 6pm to 8pm where a notification could appear at any point, around dinner time), etc. I imagine this having a stack-based implementation where the user can just add rules one after the other. 

- Pre defined string loading. The application should load strings from multiple categories. There will be categories for health, recreation, fact, and scary. And each of these categories will have strings that are relevant to the category name. The user should also be able to toggle different categories, for instance, if they are not fond of the "scary" category.

- Users should be able to have a degree of customizability over the notifications. For instance, they should be able to set the notifications so that they vibrate the phone, don't make a banner, etc. I think they may be able to handle this functionality in the settings.

- Finally, there should be an in-app purchase functionality for the PREMIUM membership. It will be a $1.99 payment for a lifetime subscription. By default, users will only have access to two or three strings from each category. Premium gives them all the strings. Users will also only be able to set two rules for notifications. Premium gives unlimited rules. Premium will also get rid of ads. Please implement a window or fragment or other view for the user to be able to purchase premium. Do not worry about implementing the IAP but please implement the style.

Those are the main requirements. Please let me know if you have any questions. If needed, you can divide your implementation across multiple responses.