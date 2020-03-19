---
---

To participate in the development of 2P-Kt, we suggest the [IntelliJ Idea](https://www.jetbrains.com/idea/download/) IDE. 
The free, _Community_ version will be fine. 

#### Recommended configuration
You will need the __Kotlin__ plugin for IntelliJ Idea. 
This is usually installed upon Idea's very first setup wizard.
However, one may easily late-install such plugin through the IDE's Plugins settings dialog.
To open such dialog, use <kbd>Ctrl</kbd>+<kbd>Shift</kbd>+<kbd>A</kbd>, then search for "Plugins"

#### Importing the project

1. Clone this repository in a folder of your preference using `git clone` appropriately

0. Open IntellJ Idea. 
If a project opens automatically, select "Close project". 
You should be on the welcome screen of IntelliJ idea, with an aspect similar to this image: 
![IntelliJ Welcome Screen](https://www.jetbrains.com/help/img/idea/2018.2/ideaWelcomeScreen.png)

0. Select "Import Project"

0. Navigate your file system and find the folder where you cloned the repository. 
**Do not select it**. 
Open the folder, and you should find a lowercase `2p-in-kotlin` folder. 
That is the correct project folder, created by `git` in case you cloned without specifying a different folder name. 
Once the correct folder has been selected, click <kbd>Ok</kbd>

0. Select "Import Project from external model"

0. Make sure "Gradle" is selected as external model tool

0. Click <kbd>Finish</kbd>

0. If prompted to override any `.idea` file, try to answer <kbd>No</kbd>. It's possible that IntelliJ refuses to proceed, in which case click <kbd>Finish</kbd> again, then select <kbd>Yes</kbd>

0. A dialog stating that "IntelliJ IDEA found a Gradle build script" may appear, in such case answer <kbd>Import Gradle Project</kbd>

0. Wait for the IDE to import the project from Gradle. The process may take several minutes, due to the amount of dependencies. Should the synchronization fail, make sure that the IDE's Gradle is configured correctly:

0. In 'Settings -> Build, Execution, Deployment -> Build Tools > Gradle', for the option 'Use Gradle from' select 'gradle-wrapper.properties file'. Enabling auto-import is also recommended
