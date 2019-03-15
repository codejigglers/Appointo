# Appointo
An android app for maintaining appointments.

## Problem Statement

Booking an appointment with multiple people in the organization and knowing the exact location in big campuse areas is not so hustle free.

## Solution

A one stop application which can book appointments and once the person has accepted , we can track the location of person.

### Libraries Used

* Retrofit(Making networking calls)
* Lottie(Using great animations)
* Room Persistence(Local database)
* Google vision(Barcode scanning)

### Permissions and Sign ups Required
* Camera Permissions
* OAuth2 
* Google Sign in
* Location

#### Working of Application
1. Sign in using your google account.
2. You will see the list of your appointments. You can delete, navigate to the location or view the description of the appointment.
3. You can create a new appointment by pressing on the + button and then scanning the barcode.
4. Scanning the barcode will show up the list of all the people linked to the bar code(In out case we are considering the professors).
5. Click on schedule to schedule the interview.
6. Enter the details that you want to see. You can also look at the calender of the person you want to make an appointment.

### Splash screen
![Splash screen](https://github.com/codejigglers/Appointo/blob/master/screesnshots/splash.gif)

### Adding Appointment
![Adding Appointment](https://github.com/codejigglers/Appointo/blob/master/screesnshots/create_appointment.gif)




