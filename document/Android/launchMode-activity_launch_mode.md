#### 启动模式

- ##### Stander

  - **默认的启动模式，启动即新建新Activity入栈**

- ##### **SingleTop**

  - **启动先从栈顶寻找是否存在相同的Activity，若存在则直接复用否则新建新Activity入栈**

- ##### SingleTask

  - **启动先从栈中遍历寻找是否存在相同的Activity，若存在则弹出该Activity之上的所有Activity，直接复用即可。不存在则新建新Activity入栈**

- ##### SingleInstance

  - **系统为该Activity分配一个单独的栈，该任务栈有且只有一个实例，新建新Activity放入单独的栈中，若已经存在该任务栈，直接复用即可**

#### taskAffinity 

- **taskAffinity 属性可用于AndroidManifest中的Application标签、Activity标签下**
- **taskAffinity 属于用于指定Activity所属的任务栈名称，默认情况下名称为包名**
- **只有当launchMode = singleTask 或者 启动Activity时设置flags=Intent.FLAG_ACTIVITY_NEW_TASK才会有效**

#### 添加Flag对Activity启动的影响

##### Intent.FLAG_ACTIVITY_NEW_TASK

- **检查是否存在与此Activity相同taskAffinity 的任务栈，若不存在，则会新建此任务栈并将该Activity压入栈顶**
- **若已经存在此任务栈，看栈顶是否是相同的Activity，是即直接复用，否则新建新Activity放入栈顶**
- **默认情况下，所有Activity所在的taskAffinity 任务栈即为包名，故当launchMode != singleIntance时，添加此flags是没有任何影响的（不会新建任务栈而是直接将该Activity压入栈顶）**
- 与SingleInstance区别
  - Intent.FLAG_ACTIVITY_NEW_TASK只是决定是否新建任务栈，而对此新建的任务栈活动实例没有数量限制，SingleInstance新建任务栈且只能存在一个活动实例
  - 默认情况下由于taskAffinity 都相同，Intent.FLAG_ACTIVITY_NEW_TASK不会创建新的任务栈，而SingleInstance只要不存在就会创建新任务栈

##### Intent.FLAG_ACTIVITY_CLEAR_TASK

- Intent.FLAG_ACTIVITY_CLEAR_TASK 这个 FLAG 必须和 Intent.FLAG_ACTIVITY_NEW_TASK 配合使用（单独使用没有任何效果）

- **一般都是配合清除栈中的实例，将此Activity压入栈中**

- ```
  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
  ```

- **检查是否存在与此Activity相同taskAffinity 的任务栈，若不存在则新建任务栈，将该Activity压入栈顶，若存在，清空此任务栈的实例，将该Activity压入栈顶**

  - 此Activity的launchMode为standard、singleTop、singleTask
  - 此Activity的launchMode为SingleInstance时
    - 此Activity未被创建过，则此配合flags失效，SingleInstance生效
    - 此Activity已被创建过，目标任务栈实例清空，新Activity压入栈顶

##### Intent.FLAG_ACTIVITY_CLEAR_TOP

- 单独设置此flag、没有设置launchMode
  - 遍历任务栈是否存在相同Activity实例，若存在，将其之上的Activity全部弹出（包括自己），并重新创建新实例压入栈中。若不存在创建新实例压入栈中
- lanchMode=singleTop
  - 结合此flag作用相当于singleTask
- 当新 Activity 的 launchMode 为 singleTask 或 singleInstance 时，该 FLAG 不起作用，由相应的 launchMode 主导。
