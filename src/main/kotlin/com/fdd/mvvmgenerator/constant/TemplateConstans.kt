package com.fdd.mvvmgenerator.constant

/**
 * @Author:miracle
 * @Date: 2020-09-10 17:05
 * @Description:
 */

object TemplateConstans {


    //XXXActivity.kt
    const val VIEW_IMPL_TEMPLATE_ACTIVITY_KOTLIN_CONTENT = """
    #if (${"$"}{PACKAGE_NAME} != "")
        package ${"$"}{PACKAGE_NAME}
    #end
        import android.os.Bundle
        import androidx.databinding.DataBindingUtil
        import androidx.lifecycle.Observer
        import com.alibaba.android.arouter.facade.annotation.Route
        import com.alibaba.android.arouter.launcher.ARouter
        import ${"$"}{PACKAGE_R}.R
    #if (${"$"}{IS_LIST} == "true")
        import ${"$"}{PACKAGE_R}.BR
    #end
    #if (${"$"}{WITH_PREFIX} == "false")
        import ${"$"}{PACKAGE_R}.databinding.Activity${"$"}{NAME}Binding
           
    #end
    #if (${"$"}{WITH_PREFIX} == "true")
        import ${"$"}{PACKAGE_R}.databinding.${"$"}{MODULE_PREFIX}Activity${"$"}{NAME}Binding
    #end
        import com.fangdd.mobile.mvvmcomponent.activity.BaseMvvmActivity
        import ${"$"}{VM_DIR}.${"$"}{NAME}VM
    #if (${"$"}{IS_LIST} == "true")
        import com.alibaba.android.vlayout.layout.LinearLayoutHelper
        import com.fangdd.mobile.base.utils.refreshmanager.FddRefreshVLayoutManager
        import com.fangdd.mobile.base.widgets.refresh.FddRefreshLayout
        import com.fangdd.mobile.base.widgets.refresh.listener.OnFddRefreshLoadMoreListener
        import com.fangdd.mobile.mvvmcomponent.adapter.ReDataBindingSubAdapter
        import com.fangdd.mobile.mvvmcomponent.factory.SimpleViewModelFactory
        import ${"$"}{ITEM_VM_DIR}.${"$"}{NAME}ItemVM
        import ${"$"}{ITEM_EVENT_DIR}.${"$"}{NAME}ItemEvent
    #end
            
        /**
         * @Author ${"$"}{AUTHOR}
         * @Date ${"$"}{DATE} ${"$"}{TIME}
         * @Description:
         *
         * -Created by fdd mvvm generator
         */
        @Route(path = "enter route path here")
        class ${"$"}{NAME}Activity : BaseMvvmActivity${"$"}{VM_GENERIC_TYPE}() #if (${"$"}{IS_LIST} == "true"), OnFddRefreshLoadMoreListener #end{
            
             companion object {
                val TAG : String = ${"$"}{NAME}Activity::class.java.simpleName
             }
        #if (${"$"}{WITH_PREFIX} == "false")
             private lateinit var mBinding: Activity${"$"}{NAME}Binding
        #end
        #if (${"$"}{WITH_PREFIX} == "true")
             private lateinit var mBinding: ${"$"}{MODULE_PREFIX}Activity${"$"}{NAME}Binding
        #end
        #if (${"$"}{IS_LIST} == "true")
             private lateinit var vLayoutManager: FddRefreshVLayoutManager
             private lateinit var m${"$"}{NAME}ItemAdapter: ReDataBindingSubAdapter<Any, ${"$"}{NAME}ItemVM>
        #end
            
             override fun getViewModelType(): Class<${"$"}{NAME}VM> {
                return ${"$"}{NAME}VM::class.java
             }
             
             override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                mBinding = DataBindingUtil.setContentView(this, R.layout.${"$"}{LAYOUT_NAME})
                mBinding.viewModel = viewModel
                initTitle()
                initView()
                 #if (${"$"}{IS_LIST} == "true") 
                 initAdapter()
                 #end
                initLiveData()
            }
            
            
            /**
             * 初始化标题栏
             */
            private fun initTitle(){
                initTitleBar(mBinding.titleBar, false)
                setTitleBarWithTitleAndDefaultLeft("title", null)
            }
            
            /**
             * 初始化View
             */
            private fun initView() {
        #if (${"$"}{IS_LIST} == "true") 
                vLayoutManager = FddRefreshVLayoutManager(mBinding.refreshLayout, mBinding.refreshLayout.recyclerView)
                vLayoutManager.setOnFddRefreshListener(this)
                vLayoutManager.setOnFddLoadmoreListener(this)
        #end           
            }
            
            
    #if (${"$"}{IS_LIST} == "true")        
            /**
              * 初始化item adapter
              */
             private fun initAdapter() {
        
                 m${"$"}{NAME}ItemAdapter = ReDataBindingSubAdapter(
                         LinearLayoutHelper(),
                         BR.viewModel,
                         R.layout.${"$"}{ITEM_LAYOUT_NAME},
                         BR.itemEvent,
                         ${"$"}{NAME}ItemEvent(),
                         SimpleViewModelFactory(${"$"}{NAME}ItemVM::class.java)
                 )
                 vLayoutManager.addAdapter(m${"$"}{NAME}ItemAdapter)
             }
    #end
            
            
            /**
             * 初始化LiveData
             */
            private fun initLiveData(){
                viewModel.showLoading.observe(this, Observer { show ->
                    show?.let {
                        if (show) {
                            showLoadingDialog(false)
                        } else {
                            dismissLoadingDialog()
                        }
                    }
                })
            }
            
    #if (${"$"}{IS_LIST} == "true")         
            override fun onLoadMore(refreshLayout: FddRefreshLayout) {
               
            }
        
            override fun onRefresh(refreshLayout: FddRefreshLayout) {
                
            }
           
    #end
}

        
"""


    //XXXFragment.kt

    const val VIEW_IMPL_TEMPLATE_FRAGMENT_KOTLIN_CONTENT = """ 
        
        #if (${"$"}{PACKAGE_NAME} != "")
            package ${"$"}{PACKAGE_NAME}
        #end
            
            import android.os.Bundle
            import android.view.LayoutInflater
            import android.view.View
            import android.view.ViewGroup
            import androidx.databinding.DataBindingUtil
            import androidx.lifecycle.Observer
            
            import ${"$"}{PACKAGE_R}.R
    #if (${"$"}{IS_LIST} == "true")
            import ${"$"}{PACKAGE_R}.BR
    #end
            
        #if (${"$"}{WITH_PREFIX} == "false")
            import ${"$"}{PACKAGE_R}.databinding.Fragment${"$"}{NAME}Binding
               
        #end
        #if (${"$"}{WITH_PREFIX} == "true")
            import ${"$"}{PACKAGE_R}.databinding.${"$"}{MODULE_PREFIX}Fragment${"$"}{NAME}Binding
        #end
            
            import com.fangdd.mobile.mvvmcomponent.fragment.BaseMvvmFragment
            import ${"$"}{VM_DIR}.${"$"}{NAME}VM
            
    #if (${"$"}{IS_LIST} == "true")
            import androidx.recyclerview.widget.RecyclerView
            import com.alibaba.android.vlayout.layout.LinearLayoutHelper
            import com.fangdd.mobile.base.utils.refreshmanager.FddRefreshVLayoutManager
            import com.fangdd.mobile.base.widgets.refresh.FddRefreshLayout
            import com.fangdd.mobile.base.widgets.refresh.listener.OnFddRefreshLoadMoreListener
            import com.fangdd.mobile.mvvmcomponent.adapter.ReDataBindingSubAdapter
            import com.fangdd.mobile.mvvmcomponent.factory.SimpleViewModelFactory
            import ${"$"}{ITEM_VM_DIR}.${"$"}{NAME}ItemVM
            import ${"$"}{ITEM_EVENT_DIR}.${"$"}{NAME}ItemEvent
    #end
            
            /**
             * @Author ${"$"}{AUTHOR}
             * @Date ${"$"}{DATE} ${"$"}{TIME}
             * @Description:
             *
             * -Created by fdd mvvm generator
             */
            class ${"$"}{NAME}Fragment : BaseMvvmFragment${"$"}{VM_GENERIC_TYPE}()#if (${"$"}{IS_LIST} == "true"), OnFddRefreshLoadMoreListener #end {
            
                companion object {
                    val TAG : String = ${"$"}{NAME}Fragment::class.java.simpleName
            
                    @JvmStatic
                    fun newInstance() =
                            ${"$"}{NAME}Fragment().apply {
                                arguments = Bundle().apply {
                                    
                                }
                            }
                }
            
        #if (${"$"}{WITH_PREFIX} == "false")
                private lateinit var mBinding: Fragment${"$"}{NAME}Binding
        #end
        #if (${"$"}{WITH_PREFIX} == "true")
                private lateinit var mBinding: ${"$"}{MODULE_PREFIX}Fragment${"$"}{NAME}Binding
        #end
        #if (${"$"}{IS_LIST} == "true")            
                private var mRecyclerView: RecyclerView? = null
                private lateinit var vLayoutManager: FddRefreshVLayoutManager
                private lateinit var m${"$"}{NAME}ItemAdapter: ReDataBindingSubAdapter<Any, ${"$"}{NAME}ItemVM>
                private var mPageIndex = 0
        #end        
                override fun getFragmentTag(): String {
                    return TAG
                }
            
                override fun getViewModelType(): Class${"$"}{VM_GENERIC_TYPE} {
                    return ${"$"}{NAME}VM::class.java
                }
            
                
            
                override fun onCreate(savedInstanceState: Bundle?) {
                    super.onCreate(savedInstanceState)
                }
            
                override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
                    mBinding = DataBindingUtil.inflate(inflater, R.layout.${"$"}{LAYOUT_NAME}, container, false)
                    mBinding.viewModel = viewModel
                    return mBinding.root
                }
            
                override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
                    super.onViewCreated(view, savedInstanceState)
            
                    initView()
                #if (${"$"}{IS_LIST} == "true") 
                    initRecyclerView()
                #end
                    initLiveData()
                }
            
               
                /**
                 * 初始化UI
                 */
                private fun initView(){
            
                }
                
            #if (${"$"}{IS_LIST} == "true")            
                /**
                 * 初始化列表
                 */
                private fun initRecyclerView(){
                    
                    mRecyclerView = mBinding.refreshLayout.recyclerView
            
                    vLayoutManager = FddRefreshVLayoutManager(mBinding.refreshLayout, mRecyclerView)
                    vLayoutManager.setOnFddRefreshLoadmoreListener(this)
            
                    // 初始化「 item adapter」
                }
                  
                /**
                  * 初始化item adapter
                  */
                 private fun init${"$"}{NAME}ListAdapter() {
            
                     m${"$"}{NAME}ItemAdapter = ReDataBindingSubAdapter(
                             LinearLayoutHelper(),
                             BR.viewModel,
                             R.layout.${"$"}{ITEM_LAYOUT_NAME},
                             BR.itemEvent,
                             ${"$"}{NAME}ItemEvent(),
                             SimpleViewModelFactory(${"$"}{NAME}ItemVM::class.java)
                     )
                     vLayoutManager.addAdapter(m${"$"}{NAME}ItemAdapter)
                 }
            #end
            
            

            
                /**
                 * 初始化LiveData
                 */
                private fun initLiveData(){
            
                    viewModel.showLoading.observe(this, Observer { show ->
                        show?.let {
                            if (show) {
                                baseActivity.showLoadingDialog(false)
                            } else {
                                baseActivity.dismissLoadingDialog()
                            }
                        }
                    })
            
                }
                
        #if (${"$"}{IS_LIST} == "true")         
            override fun onLoadMore(refreshLayout: FddRefreshLayout) {
               
            }
        
            override fun onRefresh(refreshLayout: FddRefreshLayout) {
                
            }
           
        #end
    }
        
    """


    //xxxVM.kt
    const val VM_IMPL_TEMPLATE_KOTLIN = """
        
        #if (${"$"}{PACKAGE_NAME} != "")
            package ${"$"}{PACKAGE_NAME}
        #end
            import androidx.databinding.ObservableField
            import com.fangdd.mobile.mvvmcomponent.liveevent.SingleLiveEvent
            import com.fangdd.mobile.mvvmcomponent.network.ApiExceptrion
            import com.fangdd.mobile.mvvmcomponent.network.LoadingObserver
            import com.fangdd.mobile.mvvmcomponent.viewmodel.BaseViewModel
    
            /**
             * @Author ${"$"}{AUTHOR}
             * @Date ${"$"}{DATE} ${"$"}{TIME}
             * @Description:
             *
             * -Created by fdd mvvm generator
             */
             
            class ${"$"}{NAME}VM : BaseViewModel() {
    
            }

        
    """

    //layout
    const val ACTIVITY_LAYOUT_TEMPLATE_CONTENT = """
        
        <?xml version="1.0" encoding="utf-8"?>
        <layout xmlns:android="http://schemas.android.com/apk/res/android"
            xmlns:app="http://schemas.android.com/apk/res-auto">
            
        <!--@Author: ${"$"}{AUTHOR} -->
        <!--@Date ${"$"}{DATE} ${"$"}{TIME} -->
        <!--@Description 请输入相关描述 -->
        <!--Created by fdd mvvm generator -->
            
            <data>
                <variable
                    name="viewModel"
                    type="${"$"}{VM_DIR}.${"$"}{NAME}VM"/>
            </data>
            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:orientation="vertical">
                
                <!--顶部标题-->
                <com.fangdd.mobile.basecore.widget.BaseTitleBar
                    android:id="@+id/title_bar"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"/>
                    
                 
                <com.fangdd.mobile.base.widgets.refresh.FddRecyclerRefreshLayout
                    android:id="@+id/refresh_layout"
                    android:layout_width="match_parent"
                    android:layout_height="match_parent"
                    app:fdd_hfrl_footer_type="1"
                    app:fdd_hfrl_header_type="1" />
           
                    
            </LinearLayout>
        </layout>
        
        
    """

    //layout
    const val FRAGMENT_LAYOUT_TEMPLATE_CONTENT = """
        
        <?xml version="1.0" encoding="utf-8"?>
        <layout xmlns:android="http://schemas.android.com/apk/res/android"
            xmlns:app="http://schemas.android.com/apk/res-auto">
            
        <!--@Author: ${"$"}{AUTHOR} -->
        <!--@Date ${"$"}{DATE} ${"$"}{TIME} -->
        <!--@Description 请输入相关描述 -->
        <!--Created by fdd mvvm generator -->
            
            <data>
                <variable
                    name="viewModel"
                    type="${"$"}{VM_DIR}.${"$"}{NAME}VM"/>
            </data>
            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:orientation="vertical">
 
                 
                <com.fangdd.mobile.base.widgets.refresh.FddRecyclerRefreshLayout
                    android:id="@+id/refresh_layout"
                    android:layout_width="match_parent"
                    android:layout_height="match_parent"
                    app:fdd_hfrl_footer_type="1"
                    app:fdd_hfrl_header_type="1" />
           
                    
            </LinearLayout>
        </layout>
        
        
    """


    //itemVm
    const val ITEM_VM_TEMPLATE_KOTLIN_CONTENT = """
        
    #if (${"$"}{PACKAGE_NAME} != "")
        package ${"$"}{PACKAGE_NAME}
    #end
        import androidx.databinding.ObservableField
        import com.fangdd.mobile.mvvmcomponent.viewmodel.BaseAdapterViewModel
        /**
         * @Author ${"$"}{AUTHOR}
         * @Date ${"$"}{DATE} ${"$"}{TIME}
         * @Description:
         *
         * -Created by fdd mvvm generator
         */
         class ${"$"}{NAME}ItemVM : BaseAdapterViewModel<Any>() {
        
            val itemData = ObservableField<Any>()
            //在列表中的位置
            var itemPosition = 0
            
            override fun setData(index: Int, data: Any) {
                itemPosition = index
                itemData.set(data)
            }
         }
        
    """


    //itemEvent

    const val ITEM_EVENT_TEMPLATE_KOTLIN_CONTENT = """
        
    #if (${"$"}{PACKAGE_NAME} != "")
        package ${"$"}{PACKAGE_NAME}
    #end
        import android.view.View

        import com.fangdd.mobile.mvvmcomponent.event.BaseEvent
        import ${"$"}{ITEM_VM_DIR}.${"$"}{NAME}ItemVM
        
        /**
         * @Author ${"$"}{AUTHOR}
         * @Date ${"$"}{DATE} ${"$"}{TIME}
         * @Description:
         *
         * -Created by fdd mvvm generator
         */
         class ${"$"}{NAME}ItemEvent : BaseEvent<${"$"}{NAME}ItemVM>() {

            override fun onClick(view: View?, itemVM: ${"$"}{NAME}ItemVM) {
            
            }
        }
        
    """


    //item layout
    const val ITEM_LAYOUT_TEMPLATE_CONTENT = """
        
    <?xml version="1.0" encoding="utf-8"?>
    <layout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools">

    <!--@Author: ${"$"}{AUTHOR} -->
    <!--@Date ${"$"}{DATE} ${"$"}{TIME} -->
    <!--@Description 请输入相关描述 -->
    <!--Created by fdd mvvm generator -->
    
        <data>
    
            <import type="android.view.View" />
    
            <variable
                name="itemEvent"
                type="${"$"}{ITEM_EVENT_DIR}.${"$"}{NAME}ItemEvent"/>
    
            <variable
                name="viewModel"
                type="${"$"}{ITEM_VM_DIR}.${"$"}{NAME}ItemVM"/>
        </data>
    
        <FrameLayout
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:onClick="@{(v)->itemEvent.onClick(v, viewModel)}">
        </FrameLayout>
        
    </layout>

    """


    /*
    * model
    * PACKAGE_NAME
    * SERVICE_NAME
    *
    * */
    const val MODEL_TEMPLATE = """
    
    package ${"$"}{PACKAGE_NAME}.model;
         
    import com.fangdd.mobile.mvvmcomponent.model.BaseHttpModel;
    import com.fangdd.mobile.mvvmcomponent.network.RetrofitHolder;
    import com.fangdd.mobile.realtor.common.http.LoadingObserver;  
    import com.fangdd.mobile.realtor.common.entity.EsfSearchFilterVo;
    import com.fangdd.mobile.realtor.common.http.HttpManager;
    import ${"$"}{PACKAGE_NAME}.service.${"$"}{SERVICE_NAME}Service;    
    
    import io.reactivex.Observable;
    
    /**
     * @Author ${"$"}{AUTHOR}
     * @Date ${"$"}{DATE} ${"$"}{TIME}
     * @Description:
     *
     * -Created by fdd mvvm generator
     */
     public class ${"$"}{SERVICE_NAME}Model extends BaseHttpModel {
         public ${"$"}{SERVICE_NAME}Service getService() {
            return RetrofitHolder.getInstance().getService(${"$"}{SERVICE_NAME}Service.class,
                    HttpManager.getHttpBaseUrl(),
                    HttpManager.getHeader());
         }           
    }
    """

    /*
    * kotlin model
    * PACKAGE_NAME
    * SERVICE_NAME
    *
    * */
    const val MODEL_TEMPLATE_KT = """
    
    package ${"$"}{PACKAGE_NAME}.model;
         
    import com.fangdd.mobile.mvvmcomponent.model.BaseHttpModel
    import com.fangdd.mobile.mvvmcomponent.network.RetrofitHolder
    import com.fangdd.mobile.realtor.common.http.LoadingObserver
    import com.fangdd.mobile.realtor.common.entity.EsfSearchFilterVo
    import com.fangdd.mobile.realtor.common.http.HttpManager
    import ${"$"}{PACKAGE_NAME}.service.${"$"}{SERVICE_NAME}Service   
    
    import io.reactivex.Observable
    
    /**
     * @Author ${"$"}{AUTHOR}
     * @Date ${"$"}{DATE} ${"$"}{TIME}
     * @Description:
     *
     * -Created by fdd mvvm generator
     */
     class ${"$"}{SERVICE_NAME}Model : BaseHttpModel() {
        fun getService(): ${"$"}{SERVICE_NAME}Service? {
            return RetrofitHolder.getInstance().getService(${"$"}{SERVICE_NAME}Service::class.java,
                    HttpManager.getHttpBaseUrl(), 
                    HttpManager.getHeader())
        }            
    }
    """


    /*
      * COMMENT：注释
      * METHOD_NAME：方法名
      * FORMAL_AND_ACTUAL_PARAMS:形参 + 实参
      * ACTUAL_PARAMS:实参
      * GENERIC_TYPE:泛型
      */
    const val MODEL_METHOD_TEMPLATE =
        """public void ${"$"}{METHOD_NAME}(${"$"}{FORMAL_AND_ACTUAL_PARAMS}, LoadingObserver<${"$"}{GENERIC_TYPE}> observer) {
            getService().${"$"}{METHOD_NAME}(${"$"}{ACTUAL_PARAMS})
                .compose(transResult(observer))
                .subscribe(observer);
        
        } """

    const val MODEL_METHOD_TEMPLATE_KT =
        """fun ${"$"}{METHOD_NAME}(${"$"}{FORMAL_AND_ACTUAL_PARAMS_KT}, observer:LoadingObserver<${"$"}{GENERIC_TYPE}>) {
             getService().${"$"}{METHOD_NAME}(${"$"}{ACTUAL_PARAMS})
                .compose(transResult(observer))
                .subscribe(observer)
        
        } """




    //viewModel code template
    //need success
    const val VIEW_MODEL_CODE_TEMPLATE =
     """
        val ${"$"}{METHOD_NAME}SuccessEvent = SingleLiveEvent<${"$"}{GENERIC_TYPE}>()
#if (${"$"}{NEED_FAIL_EVENT} == "true")
        val ${"$"}{METHOD_NAME}FailEvent = SingleLiveEvent<ApiExceptrion>() 
#end
        val ${"$"}{METHOD_NAME}Observer by lazy {
            LoadingObserver({result :${"$"}{GENERIC_TYPE} ->
                ${"$"}{METHOD_NAME}SuccessEvent.value = result
            }, #if (${"$"}{SHOW_LOADING} == "true") showLoading#else null#end, #if (${"$"}{NEED_FAIL_EVENT} == "true") ${"$"}{METHOD_NAME}FailEvent #else null #end)
        }
        
        /**
          ${"$"}{COMMENT}
          */
          fun ${"$"}{METHOD_NAME}(${"$"}{FORMAL_AND_ACTUAL_PARAMS_KT}) {
              ${"$"}{MODEL_NAME}.${"$"}{METHOD_NAME}(${"$"}{ACTUAL_PARAMS}, ${"$"}{METHOD_NAME}Observer)
          }
    """

    //item sub single adapter
    const val ITEM_SUB_SINGLE_ADAPTER =
        """
        val m${"$"}{NAME}Adapter = ${"$"}{ADAPTER_TYPE}(
                ${"$"}{LAYOUT_HELPER_NAME}(),
                R.layout.${"$"}{ITEM_LAYOUT_NAME},
                BR.viewModel,
                ${"$"}{NAME}ItemVM(),
                BR.itemEvent,
                ${"$"}{NAME}ItemEvent())
        mLayoutManager.addAdapter(m${"$"}{NAME}Adapter)
        """

    //item sub single adapter
    const val ITEM_SUB_ADAPTER =
        """
        val m${"$"}{NAME}Adapter:ReDataBindingSubAdapter<Any, ${"$"}{NAME}ItemVM> = ${"$"}{ADAPTER_TYPE}(
                ${"$"}{LAYOUT_HELPER_NAME}(),
                BR.viewModel,
                R.layout.${"$"}{ITEM_LAYOUT_NAME},
                BR.itemEvent,
                ${"$"}{NAME}ItemEvent(),
                SimpleViewModelFactory(${"$"}{NAME}ItemVM::class.java))
        mLayoutManager.addAdapter(m${"$"}{NAME}Adapter)    
            
        """
}