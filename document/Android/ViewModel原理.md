ViewModel原理

1. 自定义类继承ViewModel或者AndroidViewModel （其子类 扩展了getApplication方法）

```kotlin
 class BaseViewModel : ViewModel()
```

​	2.创建viewModel对象 （通过ViewModelProvider类）

```kotlin
//this即当前activity
val viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
```

​	3.ViewModelProvider类 get(Class clazz)方法

​	ViewModelStore  mViewModelStore 内部存放map及相应的put，get，clear逻辑

​	ViewModelProvider会将clazz全限定名与与class对象缓存入map中

​	4.当第一次创建时，map中并无缓存击中，会有mFactory去create（class）后再放入map中 

```java
if (mFactory instanceof KeyedFactory) {
    viewModel = ((KeyedFactory) mFactory).create(key, modelClass);
} else {
    //实际上调用SavedStateViewModelFactory.create()
    viewModel = mFactory.create(modelClass);
}
```

​	5.factory是通过传入进来的this类型来进行判断的

```java
//activity实现了ViewModelStoreOwner //HasDefaultViewModelProviderFactory
public ViewModelProvider(@NonNull ViewModelStoreOwner owner) {
    this(owner.getViewModelStore(), owner instanceof HasDefaultViewModelProviderFactory
            ? ((HasDefaultViewModelProviderFactory) owner).getDefaultViewModelProviderFactory()
            : NewInstanceFactory.getInstance());
}

//activity的getDefaultViewModelProviderFactory
public ViewModelProvider.Factory getDefaultViewModelProviderFactory() {
        if (mDefaultFactory == null) {
            mDefaultFactory = new SavedStateViewModelFactory(
                    getApplication(),
                    this,
                    getIntent() != null ? getIntent().getExtras() : null);
        }
        return mDefaultFactory;
    }
```

	6. owner.getViewModelStore() Activity中的getViewModelStore（）

```java
public ViewModelStore getViewModelStore() {
    ensureViewModelStore();
    return mViewModelStore;
}
```

```java
void ensureViewModelStore() {
    if (mViewModelStore == null) {
        //关键 非正常配置更改，拿到其对象获取到相同的mViewModelStore
        NonConfigurationInstances nc =
                (NonConfigurationInstances) getLastNonConfigurationInstance();
        if (nc != null) {
            mViewModelStore = nc.viewModelStore;
        }
        if (mViewModelStore == null) {
            mViewModelStore = new ViewModelStore();
        }
    }
}
```

​	7.SavedStateViewModelFactory.create()

```java
boolean isAndroidViewModel = AndroidViewModel.class.isAssignableFrom(modelClass);
Constructor<T> constructor;
//自定义的viemodel是否有此类型的构造器，一般无
if (isAndroidViewModel && mApplication != null) {
    constructor = findMatchingConstructor(modelClass, ANDROID_VIEWMODEL_SIGNATURE);
} else {
    constructor = findMatchingConstructor(modelClass, VIEWMODEL_SIGNATURE);
}
// doesn't need SavedStateHandle
if (constructor == null) {
    //mFactory 通过getApplication对象是否为空决定
    //NULL -> NewInstanceFactory ELSE --->AndroidViewModelFactory
    return mFactory.create(modelClass);
}
```

​	8.AndroidViewModelFactory.create 

```java
return modelClass.getConstructor(Application.class).newInstance(mApplication);
```

​	9.NewInstanceFactory

```java
return modelClass.newInstance();
```

