### Preconditions:
We have a template project for ide plugin creation; it will be used for test task implementation. It's recommended to work in the latest stable IDEA.
How to work with the project and properly run written code?
1) run gradle task `runIde`, this task will run intellij idea with preinstalled plugin from this project
2) create a new project from wizard `new project -> kotlin -> create`
3) when the project will be opened, our action will be under `Tools -> Test Task Action 1/2/3`

### Tasks:
1) In the `TestTaskAction1` class, implement the output of the installed kotlin plugin version. 
2) In the `TestTaskAction2` class, implement the output of project dependencies (aka External Libraries).
3) In the `TestTaskAction3` class, implement opening the `main.kt` file in the editor and output all line markers from it. Line markers are located in the editor on the left. They invoke some basic actions and other framework and technology-specific features.
4) Implement an action that will run the `main.kt` file and check that the task exit code is 0.
5) Imagine developers added a new template, `jvm console application` in kotlin wizard. Your task is to write a checklist/test scenario for this new template. What checks would you add in addition to the previously implemented ones (you don't have to implement them, only list them in the checklist)? 

### Useful information:
- see classes `AnActionEvent, ProjectView, PluginManager, FileEditorManager, DaemonCodeAnalyzerImpl, RunContentManager`
- https://plugins.jetbrains.com/docs/intellij/welcome.html

![img.png](readme-resources/linemarkers.png) <br>
![img2.png](readme-resources/external-libraries.png)
