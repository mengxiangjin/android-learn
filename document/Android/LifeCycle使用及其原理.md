### LifeCycle使用及其原理

#### LifeCycle的使用

作用：Lifecycle可感知activity生命周期变化，并进行相应的逻辑处理，ViewModel中对数据的读取往往都是异步操作，需要随时知道生命周期的变化，故常搭配Lifecycle使用

##### LifeCycle监听三种方式

1. Activity中直接添加监听  **LifecycleEventObserver**对象

```kotlin
//1.LifecycleEventObserver对象
//activity中getLifecycle()  Activity实现了 LifecycleOwner接口
public interface LifecycleOwner {
    /**
     * Returns the Lifecycle of the provider.
     *
     * @return The lifecycle of the provider.
     */
    @NonNull
    Lifecycle getLifecycle();
}
lifecycle.addObserver(object : LifecycleEventObserver {
    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        //监听此activity生命周期变化
        println(event.name)
    }
})
```

  2.实现**LifecycleObserver**   已被废弃

```kotlin
/*
*   1.继承 LifecycleObserver 注解方式监听 已被废弃（反射较为消耗性能）
* */
interface BaseViewModelLifecycleObserve: LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate()

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume()

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause()

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop()

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy()
}
```

```kotlin
//实现BaseViewModelLifecycleObserve接口 ，重写方法，监听
override fun onCreate() {
    println("BaseViewModel - onCreate")
}

override fun onResume() {
    println("BaseViewModel - onResume")
}

override fun onPause() {
    println("BaseViewModel - onPause")
}

override fun onStop() {
    println("BaseViewModel - onStop")
}

override fun onDestroy() {
    println("BaseViewModel - onDestroy")
}
```

3.实现**DefaultLifecycleObserver**

```kotlin
/*
* 2.实现DefaultLifecycleObserver，重写生命周期回调
*   将其对象直接addObserver即可
* */
interface IBaseViewModelLifecycleObserver: DefaultLifecycleObserver {

    override fun onCreate(owner: LifecycleOwner)
    override fun onStart(owner: LifecycleOwner)
    override fun onResume(owner: LifecycleOwner)
    override fun onPause(owner: LifecycleOwner)
    override fun onStop(owner: LifecycleOwner)
    override fun onDestroy(owner: LifecycleOwner)

}
```

```kotlin
//IBaseViewModelLifecycleObserver ，重写方法，监听
override fun onCreate(owner: LifecycleOwner) {
    println("BaseViewModel - onCreate")
}

override fun onStart(owner: LifecycleOwner) {
    println("BaseViewModel - onStart")
}

override fun onResume(owner: LifecycleOwner) {
    println("BaseViewModel - onResume")
}

override fun onPause(owner: LifecycleOwner) {
    println("BaseViewModel - onPause")
}

override fun onStop(owner: LifecycleOwner) {
    println("BaseViewModel - onStop")
}

override fun onDestroy(owner: LifecycleOwner) {
    println("BaseViewModel - onDestroy")
}
```

#### Lifecycle原理

##### addObserver方法解析  

this.getLifecycle.addObserver

```java
//Activity实现了该类
public interface LifecycleOwner {
    @NonNull
    Lifecycle getLifecycle();
}
```

```java
public abstract class Lifecycle {
    @MainThread
    public abstract void addObserver(@NonNull LifecycleObserver observer);
}
```

```java
//Lifecycle唯一子类 实际上调用的就是LifecycleRegistry类的addObserver方法
//getLifecycle返回的是mLifecycleRegistry （LifecycleRegistry对象）
@Override
public Lifecycle getLifecycle() {
    return mLifecycleRegistry;
}
public class LifecycleRegistry extends Lifecycle
```

LifecycleRegistry类中的addObserve（LifecycleObserver observe）

1. 获取当前生命周期初始状态state，将传过来的observe。共同封装成ObserverWithState对象

```java
ObserverWithState(LifecycleObserver observer, State initialState) {
	//observer 根据传入的observer类型，转换成不同的对象
    //1.lifecycle使用方式一：LifecycleEventObserver --> 原对象直接返回（不转换）
    //2.lifecycle使用方式二：LifecycleObserver（注解方式）   --> 反射方式或者生成lifecycle_adapter相关 				  			//CompositeGeneratedAdaptersObserver、ReflectiveGenericLifecycleObserver对象
    //3.lifecycle使用方式二：DefaultLifecycleObserver  ---> FullLifecycleObserverAdapter对象
    mLifecycleObserver = Lifecycling.lifecycleEventObserver(observer);
    mState = initialState;
}
```

​	2.将observer以及封装好的ObserverWithState对象作为键值对存放到LifecycleRegistry的成员变量map中

```java
private FastSafeIterableMap<LifecycleObserver, ObserverWithState> mObserverMap =
        new FastSafeIterableMap<>();
mObserverMap.putIfAbsent(observer, statefulObserver);
```

#####  onStateChanged回调触发

1. Activity的父类ComponentActivity中onCreate方法

```java
@Override
protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    //重点injectIfNeededIn（this） 即当前activity本身
    ReportFragment.injectIfNeededIn(this);
    if (mContentLayoutId != 0) {
        setContentView(mContentLayoutId);
    }
}

public static void injectIfNeededIn(Activity activity) {
    	//分情况讨论 1.Android版本
        if (Build.VERSION.SDK_INT >= 29) {
            LifecycleCallbacks.registerIn(activity);
        }
    	//创建一个没有界面的Fragment ReportFragment依附于activity
    	//生命周期同Activity感知变化
        android.app.FragmentManager manager = activity.getFragmentManager();
        if (manager.findFragmentByTag(REPORT_FRAGMENT_TAG) == null) {
            manager.beginTransaction().add(new ReportFragment(), REPORT_FRAGMENT_TAG).commit();
            manager.executePendingTransactions();
        }
    }


 static class LifecycleCallbacks implements Application.ActivityLifecycleCallbacks {
     
     	//activity注册lifecycle监听接口
     	//当activity生命周期变化后，该类的下列所属方法回触发，重点就在
     	//dispatch(activity, Lifecycle.Event.ON_CREATE);事件上
	 	static void registerIn(Activity activity) {
            activity.registerActivityLifecycleCallbacks(new LifecycleCallbacks());
        }

        @Override
        public void onActivityCreated(@NonNull Activity activity,
                @Nullable Bundle bundle) {
        }

        @Override
        public void onActivityPostCreated(@NonNull Activity activity,
                @Nullable Bundle savedInstanceState) {
            dispatch(activity, Lifecycle.Event.ON_CREATE);
        }

    	@Override
        public void onActivityPostCreated(@NonNull Activity activity,
                @Nullable Bundle savedInstanceState) {
            dispatch(activity, Lifecycle.Event.ON_CREATE);
        }
 }
```

```java
static void dispatch(@NonNull Activity activity, @NonNull Lifecycle.Event event) {
    //判断当前activity类型 （多数实现为LifecycleOwner）
    if (activity instanceof LifecycleRegistryOwner) {
        ((LifecycleRegistryOwner) activity).getLifecycle().handleLifecycleEvent(event);
        return;
    }

    if (activity instanceof LifecycleOwner) {
        //实际调用LifecycleRegistry.handleLifecycleEvent(event)
        Lifecycle lifecycle = ((LifecycleOwner) activity).getLifecycle();
        if (lifecycle instanceof LifecycleRegistry) {
            ((LifecycleRegistry) lifecycle).handleLifecycleEvent(event);
        }
    }
}
```

​	2.生命周期变化后，判断当前生命周期朝着可见或是不可见方式变化，调用其backwardPass(lifecycleOwner); forwardPass(lifecycleOwner); 

​	3.遍历map 进行回调

```java
private void backwardPass(LifecycleOwner lifecycleOwner) {
    Iterator<Map.Entry<LifecycleObserver, ObserverWithState>> descendingIterator =
            mObserverMap.descendingIterator();
    while (descendingIterator.hasNext() && !mNewEventOccurred) {
        Map.Entry<LifecycleObserver, ObserverWithState> entry = descendingIterator.next();
        ObserverWithState observer = entry.getValue();
        while ((observer.mState.compareTo(mState) > 0 && !mNewEventOccurred
                && mObserverMap.contains(entry.getKey()))) {
            Event event = Event.downFrom(observer.mState);
            if (event == null) {
                throw new IllegalStateException("no event down from " + observer.mState);
            }
            pushParentState(event.getTargetState());
            //回调 observe即是包装好的ObserverWithState类
            observer.dispatchEvent(lifecycleOwner, event);
            popParentState();
        }
    }
}

 static class ObserverWithState {
        State mState;
        LifecycleEventObserver mLifecycleObserver;

        ObserverWithState(LifecycleObserver observer, State initialState) {
            mLifecycleObserver = Lifecycling.lifecycleEventObserver(observer);
            mState = initialState;
        }

        void dispatchEvent(LifecycleOwner owner, Event event) {
            State newState = event.getTargetState();
            mState = min(mState, newState);
            //回调出去 mLifecycleObserver即是上述返回的三个对象
            mLifecycleObserver.onStateChanged(owner, event);
            mState = newState;
        }
    }
```