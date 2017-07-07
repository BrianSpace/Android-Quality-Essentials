# AndroidQualityEssentials
Improve Android code quality with static code analysis and runtime check:
* Naming Convention
* Code Style
* Potential Bugs
* Potential ANR (slow operations in the main thread)
* Resource and Memory Leaks
The tools used: checkstyle, findbugs, PMD, Android Lint, StrictMode and LeakCanary.
## Get started
1. Sync the code of this project and copy the [quality](quality) directory to the root directory of your project.
2. Add the following line in your `build.gradle`:
```
apply from: '../quality/static_analysis.gradle'
```
3. Add Lint options
```
android {
    ...
    lintOptions {
        // Turn off analysis progress reporting by lint
        quiet true
        // Stop the gradle build if errors are found
        abortOnError true
        // Do not ignore lint warnings
        ignoreWarnings false
        // Treat warnings as errors
        warningsAsErrors true

        // Ignore rules list
        ignore 'GoogleAppIndexingWarning' // Remove this if the app support App Indexing
    }
    ...
}
```
4. Add dependency for LeakCanary in your `build.gradle`:
```gradle
 dependencies {
   ...
   debugCompile 'com.squareup.leakcanary:leakcanary-android:1.5.1'
   releaseCompile 'com.squareup.leakcanary:leakcanary-android-no-op:1.5.1'
   testCompile 'com.squareup.leakcanary:leakcanary-android-no-op:1.5.1'
   ...
 }
```
5. In your `Application` class (and not forget to add to the manifest):
```java
public class AndroidQualityEssentialsApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyDeath()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyDeath()   // If violations are in Android itself or 3rd-party libs, use penaltyLog.
                    .build());

            // Avoid the process dedicated to LeakCanary for heap analysis.
            if (!LeakCanary.isInAnalyzerProcess(this)) {
                LeakCanary.install(this);
            }
        }
        ...
    }
}
```
6. Run `gradlew check` in the console to start the static analysis, and run your debug version application for runtime check. Now you can add the check step to your CI process.
The analysis report will be located in the `build/reports/` directory of the project in which you apply the `static_analysis.gradle`.

## What will be checked?
### Naming Conventions
Naming conventions are defined in the [quality/checkstyle/naming_convention.xml](quality/checkstyle/naming_convention.xml) file. The following rules are defined:
1. Java files:
   - Should use Camel case.
   - Test files should be named "*Test.java" or "Base*.java".
2. Resource files:
   - Resource files should use "snake" names (lower case, concatenated by underscore).
   - Drawables should begin with "bg_", "ic_" or "img_".
   - Layouts should begin with "activity_", "fragment_", "view_", "dialog_", "item_" or "btn_".
   - Values should begin with "attrs_", "colors_", "dimens_", "strings_" or "styles_".
You can modify the regular expressions as you need for your own project.
Run `gradlew checkFileNames` if you would like to check only the naming convention.
### Code Styles with CheckStyle
[CheckStyle](http://checkstyle.sourceforge.net/) is used to check the Java code style. [Style rules](quality/checkstyle/google_checks.xml) are based on [Google Java Style Guide](https://github.com/checkstyle/checkstyle/blob/master/src/main/resources/google_checks.xml) with the following changes:
   - Severity: changed to "error".
   - Line length: changed to 120.
   - Indentation: tab size changed to 4.
   - MethodName: underscore is not allowed.
   - ConstantName: all upper case, underscore allowed.
Run `gradlew checkCodeStyle` if you would like to check only the code style.
### Static Analysis with Findbugs
[FindBugs](http://findbugs.sourceforge.net/) scan your code for [patterns](http://findbugs.sourceforge.net/bugDescriptions.html) that may result in bugs. The files to be excluded from the analysis is defined [here](quality/findbugs/android-exclude-filter.xml).
Run `gradlew findBugs` if you would like to run findbugs only.
### Static Analysis with PMD
[PMD](https://pmd.github.io/) is a static code analyzer that can detect common programming flaws. Rules are defined in `quality/pmd/pmd-ruleset.xml`. Full list of the rules can be found [here](https://pmd.github.io/pmd-5.8.0/pmd-java/rules/index.html). 
Run `gradlew pmdCheck` if you would like to run PMD only.
### Static Analysis with Lint
[Android Lint](https://developer.android.com/studio/write/lint.html) is a Android specific static code analysis tool. The full list of checks is [here](http://tools.android.com/tips/lint-checks). 
Run `gradlew lint` if you would like to run Lint rules only.
### Runtime check with StrictMode
[StrictMode](https://developer.android.com/reference/android/os/StrictMode.html) is very useful to detect slow operations in UI thread and resource leaks.
Specifically, [ThreadPolicy](https://developer.android.com/reference/android/os/StrictMode.ThreadPolicy.html) will detect disk/network I/O and slow operations in UI thead, whereas [VmPolicy](https://developer.android.com/reference/android/os/StrictMode.VmPolicy.html) will detect resource leaks.
For details, see the documents for [ThreadPolicy.Builder](https://developer.android.com/reference/android/os/StrictMode.ThreadPolicy.Builder.html)
and [VmPolicy.Builder](https://developer.android.com/reference/android/os/StrictMode.VmPolicy.Builder.html)

### Memory leak Detection with LeakCanary
[LeakCanary](https://github.com/square/leakcanary) can help to detect memory leaks. It has a very nice UI to report the leaks and show the whole reference chain so that you can easily locate where to fix the leaks.

## Thanks
The static analysis config for findbugs and PMD is based on <https://github.com/ribot/android-boilerplate>.
The method to run the checks in parallel is from <https://medium.com/@dpreussler/speed-up-your-android-gradle-build-baa329cdb836>.

License
=======

    The MIT License

    Copyright (c) 2017-2017 AndroidQualityEssentials project contributors

    https://github.com/BrianSpace/AndroidQualityEssentials/graphs/contributors

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in
    all copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
    THE SOFTWARE.