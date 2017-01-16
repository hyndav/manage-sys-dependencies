
## Build ##

Run the following command to build the application zip file which can be unzipped and run later

    ./gradlew distZip

### Run ###

Run the application using gradle (runtime environment will be loaded from `properties/config.properties`). The command line arguments are specified in `build.gradle` under the run task.

    ./gradlew run

### Setup IDE ###

For generating IDE configurations for eclipse

    ./gradlew eclipse
    
### For Docker ###

Run `installDist` task and then run the docker build

    ./gradlew installDist
  
