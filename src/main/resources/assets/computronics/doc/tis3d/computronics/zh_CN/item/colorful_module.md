# 变色模块

![过于漂亮了。](item:computronics:modules.tis3d@0)

激活后，变色模块会持续从其全部四个端口读取数值。读取后，此模块会将自身颜色设定为读取到的值。

颜色由一个15位二进制数定义。其低5位定义了蓝色叠加量，中间5位定义了绿色叠加量，高5位定义了红色叠加量。例如，二进制数`000000000011111`（使用时需转换为十进制）代表纯蓝色。
