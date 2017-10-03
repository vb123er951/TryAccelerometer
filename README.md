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

Note that during the collection, need to keep the screen on

Referenced paper: A Deep Learning Approach to Human Activity Recognition Based on Single Accelerometer

### Activity version ###
Run the whole app in an activity (both accelerometer and user control)

### Service version ###
Run the accelerometer on android service, and run the user control part on an activity
