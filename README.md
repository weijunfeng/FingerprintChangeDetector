# FingerprintChangeDetector
用于检测指纹变更程序
背景：在android10及以上无法通过获取到指纹对应的id，即无法通过判断指纹id变更来按断指纹是否变更

## 整个程序设计符合SOLID原则
1. 每一个类都单一职责
2. 整个程序对修改关闭对扩展开放
3. 每个类都只实现其需要的接口
4. 可使用子类实例替换而不用大量修改程序
5. 高层次模块通过接口依赖底层次模块，不依赖具体实现