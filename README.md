![](https://github.com/wniemiec-task-java/scheduler/blob/master/docs/img/logo/logo.jpg)

<h1 align='center'>Scheduler</h1>
<p align='center'>Schedule routines to run after a certain time or whenever the the timer expires.</p>
<p align="center">
	<a href="https://github.com/wniemiec-task-java/scheduler/actions/workflows/windows.yml"><img src="https://github.com/wniemiec-task-java/scheduler/actions/workflows/windows.yml/badge.svg" alt=""></a>
	<a href="https://github.com/wniemiec-task-java/scheduler/actions/workflows/macos.yml"><img src="https://github.com/wniemiec-task-java/scheduler/actions/workflows/macos.yml/badge.svg" alt=""></a>
	<a href="https://github.com/wniemiec-task-java/scheduler/actions/workflows/ubuntu.yml"><img src="https://github.com/wniemiec-task-java/scheduler/actions/workflows/ubuntu.yml/badge.svg" alt=""></a>
	<a href="https://codecov.io/gh/wniemiec-task-java/scheduler"><img src="https://codecov.io/gh/wniemiec-task-java/scheduler/branch/master/graph/badge.svg?token=R2SFS4SP86" alt="Coverage status"></a>
	<a href="http://java.oracle.com"><img src="https://img.shields.io/badge/java-11+-D0008F.svg" alt="Java compatibility"></a>
	<a href="https://mvnrepository.com/artifact/io.github.wniemiec-task-java/scheduler"><img src="https://img.shields.io/maven-central/v/io.github.wniemiec-task-java/scheduler" alt="Maven Central release"></a>
	<a href="https://github.com/wniemiec-task-java/scheduler/blob/master/LICENSE"><img src="https://img.shields.io/github/license/wniemiec-task-java/scheduler" alt="License"></a>
</p>
<hr />

## ❇ Introduction
Scheduler allows you to perform operations with routines so that they are executed according to a criterion.

## ❓ How to use
1. Add one of the options below to the pom.xml file: 

#### Using Maven Central (recomended):
```
<dependency>
  <groupId>io.github.wniemiec-task-java</groupId>
  <artifactId>scheduler</artifactId>
  <version>LATEST</version>
</dependency>
```

#### Using GitHub Packages:
```
<dependency>
  <groupId>wniemiec.task.java</groupId>
  <artifactId>scheduler</artifactId>
  <version>LATEST</version>
</dependency>
```

2. Run
```
$ mvn install
```

3. Use it
```
[...]

import wniemiec.task.java.Scheduler;

[...]

Scheduler.setTimeout(() -> { System.out.println("Hello..."); }, 1000);
Scheduler.setTimeout(() -> { System.out.println("World!"); }, 1000);
```

## 📖 Documentation
|        Property        |Parameter type|Return type|Description|Default parameter value|
|----------------|-------------------------------|-----------------------------|--------|
|setTimeout |`routine: Routine, delay: long`|`long`|Sets a timer which executes a routine once the timer expires| - |
|setInterval |`routine: Routine, delay: long`|`long`|Repeatedly calls a routine with a fixed time delay between each call| - |
|clearInterval |`id: long`|`void`|Cancels a timed, repeating action| - |
|clearTimeout |`id: long`|`void`|Cancels a timed action| - |
|clearAllTimeout | `void`|`void`|Clear all timeouts| - |
|clearAllIntervals | `void`|`void`|Clear all intervals| - |
|setTimeoutToRoutine|`routine: Routine, delay: long`|`long`|Runs a routine within a timeout. If the routine does not end on time, an interrupt signal will be sent to it| - |

## 🚩 Changelog
Details about each version are documented in the [releases section](https://github.com/williamniemiec/wniemiec-task-java/scheduler/releases).

## 🤝 Contribute!
See the documentation on how you can contribute to the project [here](https://github.com/wniemiec-task-java/scheduler/blob/master/CONTRIBUTING.md).

## 📁 Files

### /
|        Name        |Type|Description|
|----------------|-------------------------------|-----------------------------|
|dist |`Directory`|Released versions|
|docs |`Directory`|Documentation files|
|src     |`Directory`| Source files|
