# AndroidQualityEssentials [[English Version](README.md)]
通过静态代码分析和运行时的检查来提高安卓代码质量:
* 命名规范（特别是资源文件的命名）：[CheckStyle](http://checkstyle.sourceforge.net/)
* 代码风格：[CheckStyle](http://checkstyle.sourceforge.net/)
* 潜在的缺陷：[FindBugs](http://findbugs.sourceforge.net/)、[PMD](https://pmd.github.io/)以及[Android Lint](https://developer.android.com/studio/write/lint.html)
* 潜在的ANR (由主线程中的耗时操作引起的Android Not Responding)：[StrictMode](https://developer.android.com/reference/android/os/StrictMode.html)
* 资源和内存的泄露：[StrictMode](https://developer.android.com/reference/android/os/StrictMode.html)和[LeakCanary](https://github.com/square/leakcanary)

最好的使用方式，是在创建新项目的时候就引入这些规则，在每次check-in之前修复检查到的任何错误（作为持续集成的一个检查步骤）。否则的话，面对成百上千的错误，是需要很大的勇气和毅力去逐个修复的。『从入门到放弃』并不是解决代码质量问题的正确态度。

## 目录
 - [如何使用](#%E5%A6%82%E4%BD%95%E4%BD%BF%E7%94%A8)
 - [常见问题的修复](#%E5%B8%B8%E8%A7%81%E9%97%AE%E9%A2%98%E7%9A%84%E4%BF%AE%E5%A4%8D)
    - [允许成员变量以"m"开头](#%E5%85%81%E8%AE%B8%E6%88%90%E5%91%98%E5%8F%98%E9%87%8F%E4%BB%A5m%E5%BC%80%E5%A4%B4)
    - [给变量、参数、域自动增加"final"关键字](#%E7%BB%99%E5%8F%98%E9%87%8F%E5%8F%82%E6%95%B0%E5%9F%9F%E8%87%AA%E5%8A%A8%E5%A2%9E%E5%8A%A0final%E5%85%B3%E9%94%AE%E5%AD%97)
    - [创建工具类（Utility Class）](#%E5%88%9B%E5%BB%BA%E5%B7%A5%E5%85%B7%E7%B1%BButility-class)
 - [PMD规则的权衡](#pmd%E8%A7%84%E5%88%99%E7%9A%84%E6%9D%83%E8%A1%A1)
 - [检查详情](#%E6%A3%80%E6%9F%A5%E8%AF%A6%E6%83%85)
    - [命名规范](#%E5%91%BD%E5%90%8D%E8%A7%84%E8%8C%83)
    - [使用CheckStyle检查代码风格](#%E4%BD%BF%E7%94%A8checkstyle%E6%A3%80%E6%9F%A5%E4%BB%A3%E7%A0%81%E9%A3%8E%E6%A0%BC)
    - [静态检查：Findbugs](#%E9%9D%99%E6%80%81%E6%A3%80%E6%9F%A5findbugs)
    - [静态检查：PMD](#%E9%9D%99%E6%80%81%E6%A3%80%E6%9F%A5pmd)
    - [静态检查：Android Lint](#%E9%9D%99%E6%80%81%E6%A3%80%E6%9F%A5android-lint)
    - [运行时检查：StrictMode](#%E8%BF%90%E8%A1%8C%E6%97%B6%E6%A3%80%E6%9F%A5strictmode)
    - [使用LeakCanary发现内存泄露](#%E4%BD%BF%E7%94%A8leakcanary%E5%8F%91%E7%8E%B0%E5%86%85%E5%AD%98%E6%B3%84%E9%9C%B2)
 - [致谢](#%E8%87%B4%E8%B0%A2)
 - [License](#license)
 
## 如何使用
1. 把[quality](quality)目录加入你的项目。
    * 可以直接拷贝：
        * 同步项目代码，拷贝[quality](quality)目录到你项目的根目录下。
        * 在项目的`build.gradle`开头加入下面这一行：
        ```
        apply from: '../quality/static_analysis.gradle'
        ```
    * 或者作为remote module加入你的项目。就像在我的项目[Android-App-Architecture-MVVM-Databinding](https://github.com/BrianSpace/Android-App-Architecture-MVVM-Databinding)里面这样：
        * 加入remote module：
        ```bash
        git remote add analysis https://github.com/BrianSpace/Android-Quality-Essentials.git
        ```
        * 然后加入`build.gradle`：
        ```
        apply from: '../analysis/quality/static_analysis.gradle'
        ```
3. 加入Lint配置
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
4. 在`build.gradle`中引入LeakCanary的依赖：
```gradle
 dependencies {
   ...
   debugCompile 'com.squareup.leakcanary:leakcanary-android:1.5.1'
   releaseCompile 'com.squareup.leakcanary:leakcanary-android-no-op:1.5.1'
   testCompile 'com.squareup.leakcanary:leakcanary-android-no-op:1.5.1'
   ...
 }
```
5. 在安卓应用的`Application`类的onCreate中加入下列代码（别忘了加入[AndroidManifest.xml](app/src/main/AndroidManifest.xml)中）：
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
6. 在命令行中运行`gradlew check`来进行静态代码检查。
-  分析报告在应用了`static_analysis.gradle`的项目的`build/reports/`目录下。
7. 运行Debug版本的应用进行运行时检查。
8. 把check过程加入持续集成的步骤中。

## 常见问题的修复
### 允许成员变量以"m"开头
在[命名规则](quality/checkstyle/google_checks.xml)中不允许像"mMember"这样以单个小写字母为前缀的成员变量命名方式。但是如果你喜欢这种命名方式，可以把"`MemberName`"模块的"`format`"属性改成"`^[a-z][a-zA-Z0-9]*$`"。也可以改成"`^m[A-Z][a-zA-Z0-9]*$`"来强制使用以"m"为前缀的成员变量命名方式。
### 给变量、参数、域自动增加"final"关键字
1. 在Android Studio中打开"Analyze"->"Run Inspection by Name..."菜单，搜索"be final"，然后运行在"Java"->"Code style issues"下的以下两条规则：
    * Field may be 'final'
    * Local variable or parameter may be final
2. 每次检查完成后，在结果面板中点击"Make final"按钮，就可以自动添加"final"关键字了。
### 创建工具类（Utility Class）
对于PMD规则[UseUtilityClass](https://pmd.github.io/pmd-6.2.0/pmd_rules_java_design.html#useutilityclass)，如果你的类只有静态成员变量和函数，会提示你创建Utility Class。你需要：
  * 把类定义为final。
  * 创建一个私有的构造函数并抛出异常，以免被实例化。
例子：
```java
public final class FileUtil {

    private static Context appContext;

    private FileUtil() throws InstantiationException {
        throw new InstantiationException("Utility class FileUtil should not be instantiated!");
    }

    public static void init(final Context context) {
        appContext = context.getApplicationContext();
    }

    /**
     * Get available cache directory. Prefer external over internal.
     */
    @NonNull
    public static File getAvailableCacheDir() {
        final File externalCacheDir = appContext.getExternalCacheDir();
        return externalCacheDir == null ? appContext.getCacheDir() : externalCacheDir;
    }
}
```
## PMD规则的权衡
代码检查的规则都是人们长期总结出来的最佳实践，但并不是放之四海而皆准的真理。有些规则需要根据项目的具体需求来确定是否采用：
1. [AccessorMethodGeneration](https://pmd.github.io/pmd-6.2.0/pmd_rules_java_bestpractices.html#accessormethodgeneration)
这个规则更关注性能以及减少方法数，单就我个人而言更关心信息的封装，所以我在PMD配置中排除了这个规则。如果你更关心运行的性能以及方法数（以避免Multi-dex的问题），那你就应该把这个规则包含进来。
2. [GenericsNaming](https://pmd.github.io/pmd-6.2.0/pmd_rules_java_codestyle.html#genericsnaming)
这个规则要求泛型的参数都采用单个大写字母。从可读性的角度我更喜欢更有意义的命名方式：以"T"结尾的类型命名，如`ItemTypeT`。如果你更喜欢看起来更简单的单字母命名，可以把这个规则从exclude中去掉。
## 检查详情
### 命名规范
命名规范定义在[quality/checkstyle/naming_convention.xml](quality/checkstyle/naming_convention.xml)文件中。
1. Java文件：
   - 使用[驼峰（Camel）命名法](https://en.wikipedia.org/wiki/Camel_case)
   - 测试文件须命名为"*Test.java"或者"Base*.java"
2. 资源文件：
   - 使用[蛇形命名法](https://en.wikipedia.org/wiki/Snake_case)(全小写，以下划线分隔)。
   - Drawables以"bg_"、"ic_"或者"img_"开头。
   - Layouts布局文件以"activity_"、"fragment_"、"view_"、"dialog_"、"item_"或"btn_"开头。
   - Values以"attrs_"、"colors_"、"dimens_"、"strings_"或"styles_"开头。

你可以根据项目的命名规范修改相关的正则表达式。
如果只想检查命名规范，可以运行`gradlew checkFileNames`。
### 使用CheckStyle检查代码风格
[CheckStyle](http://checkstyle.sourceforge.net/)被用来检查Java代码风格。[代码风格](quality/checkstyle/google_checks.xml)定义基于[Google Java Style Guide](https://github.com/checkstyle/checkstyle/blob/master/src/main/resources/google_checks.xml)，并做了如下修改：
   - Severity: 改为"error"
   - Line length: 改为120
   - Indentation: tab大小改为4
   - MethodName: 不允许使用下划线
   - ConstantName: 全大写，允许下划线

如果只想检查代码风格，可以运行`gradlew checkCodeStyle`。
如果需要在检查时排除一些文件（比如来自第三方的代码），可以在[static_analysis.gradle](quality/static_analysis.gradle)中的`checkCodeStyle`任务内增加`exclude`项目。
### 静态检查：Findbugs
[FindBugs](http://findbugs.sourceforge.net/)检查代码中可能会导致Bug的[模式](http://findbugs.sourceforge.net/bugDescriptions.html)。检查中需要排除在外的文件定义在[这儿](quality/findbugs/android-exclude-filter.xml).
如果只想进行findbugs检查，可以运行`gradlew findBugs`。
### 静态检查：PMD
[PMD](https://pmd.github.io/)用来检查代码中常见缺陷的工具。规则定义在[quality/pmd/pmd-ruleset.xml](quality/pmd/pmd-ruleset.xml)中。规则的完整列表参见[这儿](https://pmd.github.io/pmd-6.2.0/pmd_rules_java.html). 
如果只想进行PMD检查，可以运行`gradlew pmdCheck`。
### 静态检查：Android Lint
[Android Lint](https://developer.android.com/studio/write/lint.html)为安卓特别开发的代码检查工具。完整的检查列表参考[这儿](http://tools.android.com/tips/lint-checks). 
如果只想进行Lint检查，可以运行`gradlew lint`。
### 运行时检查：StrictMode
[StrictMode](https://developer.android.com/reference/android/os/StrictMode.html)对发现可能会导致UI无响应操作以及资源泄露很有效。
[ThreadPolicy](https://developer.android.com/reference/android/os/StrictMode.ThreadPolicy.html)用来检查UI线程中的磁盘、网络I/O以及耗时操作；[VmPolicy](https://developer.android.com/reference/android/os/StrictMode.VmPolicy.html)则用来检查未释放的资源。
详情请参考[ThreadPolicy.Builder](https://developer.android.com/reference/android/os/StrictMode.ThreadPolicy.Builder.html)
以及[VmPolicy.Builder](https://developer.android.com/reference/android/os/StrictMode.VmPolicy.Builder.html)。

### 使用LeakCanary发现内存泄露
[LeakCanary](https://github.com/square/leakcanary)能帮助你发现内存泄露。它有自己单独的UI用来汇报内存泄露，列出的引用链让你能很容易地找到该从哪里断开链条，修复泄露。

## 致谢
* 使用findbugs和PMD的部分参考了<https://github.com/ribot/android-boilerplate>.

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

