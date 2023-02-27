# FingerprintChangeDetector
用于检测指纹变更程序
背景：在android10及以上无法通过获取到指纹对应的id，即无法通过判断指纹id变更来按断指纹是否变更

## 整个程序设计符合SOLID原则
1. 每一个类都单一职责
2. 整个程序对修改关闭对扩展开放
3. 每个类都只实现其需要的接口
4. 可使用子类实例替换而不用大量修改程序
5. 高层次模块通过接口依赖底层次模块，不依赖具体实现

## 依赖
在项目bulid.gralde中添加如下依赖
```gradle
    implementation 'io.github.wjf510.fingerprint.change:detector-release:1.0.1-RELEASE'
```
## 实现说明
1. 该库支持Android 6.0及之后版本处理指纹变更校验，Android在6.0及之后版本开始支持指纹识别
2. 在Android 6版本和Android 10及以后版本实现上有些不同，Android 6 到10版本可用通过反射获取到指纹绑定的id，在Android10之后版本无法获取到，使用创建一个密钥，该密钥在生物识别注册时失效的方式来处理指纹的变更

## 使用
1. 创建`FingerprintChangeDetector`实例
```kotlin
    val fingerprintChangeDetector by lazy {
        FingerprintChangeDetector.createDetector(
            ConsoleLogger(),
            SharedPreferencesStorage(baseContext, "fingerprintChange"),
            AndroidFingerprintPlatform(baseContext, consoleLogger)
        )
    }
```
2. 处理业务逻辑后调用保存指纹id
```kotlin
    fingerprintChangeDetector.saveEnrollIds()
```
3. 判断指纹是否判断
```kotlin
    fingerprintChangeDetector.isChanged()
```
4. 处理完指纹变更逻辑后，删除保存的指纹id
```kotlin
    fingerprintChangeDetector.deleteEnrollIds()
```







