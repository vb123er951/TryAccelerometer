# TryAccelerometer

Trying to work with Android cell phone accelerometer

To record the accelerometer values, and using it to recognize human activities

Activities have:
- walk
- sit
- stand
- upstairs
- downstairs
- run

Collecting data rate is 50Hz (1 second 50 data), each collection time must be at least 5 seconds

Referenced paper: A Deep Learning Approach to Human Activity Recognition Based on Single Accelerometer

### Activity version ###
Run the whole app in an activity (both accelerometer and user control)

### Service version ###
Run the accelerometer on android service, and run the user control part on an activity (the apk is for this version)

### How to use ###
When open the app, there has two buttons: WRITE and STOP

Push WRITE to start recording; push STOP to stop recording

After push WRITE, you can put your cell phone into your right pocket of the pants, then you can do one of the activities metioned above (walk/sit/stand/upstairs/downstairs/run)

Doing activity for a while, then you can take out the phone and stop the recording

The recording file named "output.txt" will locate in the cell phone internal storage, under a directory called "test"

There are somethings to notice:
- collection time must be at least 5 seconds
- during the collection, need to keep the screen on
- can only do one action once a record period

### The output file ###
The output file: output.txt is the result of user accelerometer values recording

The file format is: time, x-axis values, y-axis values, z-axis values, and at the end of file there is a label, sign for this collection
