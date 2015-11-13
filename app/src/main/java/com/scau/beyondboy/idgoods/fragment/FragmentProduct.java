package com.scau.beyondboy.idgoods.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.scau.beyondboy.idgoods.ProductDetailActivity;
import com.scau.beyondboy.idgoods.R;
import com.scau.beyondboy.idgoods.consts.Consts;
import com.scau.beyondboy.idgoods.manager.ThreadManager;
import com.scau.beyondboy.idgoods.model.ProductBean;
import com.scau.beyondboy.idgoods.model.TimeProductBean;
import com.scau.beyondboy.idgoods.utils.LoadImageUtils;
import com.scau.beyondboy.idgoods.utils.NetWorkHandlerUtils;
import com.scau.beyondboy.idgoods.utils.NetworkUtils;
import com.scau.beyondboy.idgoods.utils.ShareUtils;
import com.scau.beyondboy.idgoods.utils.ToaskUtils;
import com.scau.beyondboy.idgoods.view.SlideListView;

import org.litepal.crud.DataSupport;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;

/**
 * Author:beyondboy
 * Gmail:xuguoli.scau@gmail.com
 * Date: 2015-09-20
 * Time: 15:10
 * 我的产品界面
 */
public class FragmentProduct extends Fragment
{
    private static final String TAG = FragmentProduct.class.getName();
    //private MainActivity mMainActivity;
    @Bind(R.id.product_slidelistview)
    SlideListView mProductListView;
    private List<Object> mProductBeanList=new LinkedList<>();
    /**保存时间对应的产品个数*/
    private Map<String,Integer> mDateCountProduct=new LinkedHashMap<>();
    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        //mMainActivity=(MainActivity)context;
        ThreadManager.scoolPoolSize=3;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view=inflater.inflate(R.layout.myproduct,container,false);
        ButterKnife.bind(this, view);
        View empty=inflater.inflate(R.layout.list_item_empty_view,container,false);
        //((ViewGroup)mProductListView.getParent()).addView(empty);
        mProductListView.setEmptyView(empty);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        loadDate();
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
       // mMainActivity.mSearchView.setVisibility(View.GONE);
    }

    /**
     * 加载数据
     */
    private void loadDate()
    {
        if(NetworkUtils.isNetworkReachable())
        {
            ArrayMap<String,String> params=new ArrayMap<>(1);
            params.put(Consts.USERID_KEY, ShareUtils.getUserId());
            NetWorkHandlerUtils.<TimeProductBean>postAsynHandler(Consts.GET_PRODUCT, params, null, null, new NetWorkHandlerUtils.PostSuccessCallback<List<TimeProductBean>>()
            {
                @Override
                public void success(final List<TimeProductBean> result)
                {
                    ThreadManager.addSingalExecutorTask(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            Log.i(TAG, "拉取");
                            loadNetWordData(result);
                        }
                    });
                }
            }, new TypeToken<List<TimeProductBean>>()
            {
            }.getType(), new NetWorkHandlerUtils.PostFailCallback()
            {
                @Override
                public void fail()
                {
                    ThreadManager.addSingalExecutorTask(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            loadLocalData();
                        }
                    });
                }
            });
        }
        else
        {
            ThreadManager.addSingalExecutorTask(new Runnable()
            {
                @Override
                public void run()
                {
                    loadLocalData();
                }
            });
        }
    }
    private void loadLocalData()
    {
        List<TimeProductBean> timeProductBeanList=DataSupport.findAll(TimeProductBean.class,true);
        for (TimeProductBean timeProductBean : timeProductBeanList)
        {
            String dateTimedb=timeProductBean.getDateTime();
            String[] dateArray=dateTimedb.split("/");
            if(dateArray.length!=0)
            {
                dateTimedb=dateArray[0]+"年"+dateArray[1]+"月"+dateArray[2]+"日";
            }
            mProductBeanList.add(dateTimedb);
            for(ProductBean productBean:timeProductBean.getBeanList())
            {
                productBean.setDateTime(dateTimedb);
                mProductBeanList.add(productBean);
            }
            mDateCountProduct.put(dateTimedb,timeProductBean.getBeanList().size());
        }
        getActivity().runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                mProductListView.setAdapter(new ProductAdapter());
            }
        });
    }

    private void loadNetWordData(List<TimeProductBean> result)
    {
        for (TimeProductBean timeProductBean : result)
        {
            //存入数据库时间，由于数据库识别不了中文，转成全英的
            String dateTimedb = timeProductBean.getDateTime().replaceAll("[\u4e00-\u9fa5]+", "/");
            dateTimedb = dateTimedb.substring(0, dateTimedb.length() - 1);
            TimeProductBean timeProductBeandb;
            List<TimeProductBean> timeProductBeanListdb = DataSupport.where("datetime =?", dateTimedb).find(TimeProductBean.class);
            if (timeProductBeanListdb == null || timeProductBeanListdb.size() == 0)
            {
                timeProductBeandb = new TimeProductBean();
                //保存到数据库
                timeProductBeandb.setDateTime(dateTimedb);
            } else
            {
                //返回原来数据
                timeProductBeandb = DataSupport.where("datetime =?", dateTimedb).find(TimeProductBean.class).get(0);
            }
            mProductBeanList.add(timeProductBean.getDateTime());
            for (ProductBean productBean : timeProductBean.getBeanList())
            {
                productBean.setDateTime(timeProductBean.getDateTime());
                //添加数据库中
                if (DataSupport.where("serialnumber=? and name=?", productBean.getSerialNumber(), productBean.getName()).find(ProductBean.class).size() == 0)
                {
                    productBean.save();
                }
                //添加不重复到数据库中
                timeProductBeandb.getBeanList().add(productBean);
                mProductBeanList.add(productBean);
            }
            //提交到数据库中
            timeProductBeandb.save();
            mDateCountProduct.put(timeProductBean.getDateTime(), timeProductBean.getBeanList().size());
        }
        getActivity().runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                mProductListView.setAdapter(new ProductAdapter());
            }
        });
    }

    private class ProductAdapter extends ArrayAdapter<Object>
    {
        public ProductAdapter()
        {
            super(getActivity(),0,mProductBeanList);
            if(mProductBeanList==null||mProductBeanList.size()==0)
            {
                ToaskUtils.displayToast("没有数据");
            }
        }

        @Override
        public int getViewTypeCount()
        {
            return 2;
        }

        @Override
        public Object getItem(int position)
        {
            return mProductBeanList.get(position);
        }

        @Override
        public int getItemViewType(int position)
        {
            if(getItem(position) instanceof ProductBean)
            {
                return 0;
            }
            else
            {
                return 1;
            }
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent)
        {
            if(mProductBeanList.get(position) instanceof ProductBean)
           {
                Holder2 holder;
                if(convertView==null)
                {
                    convertView=LayoutInflater.from(parent.getContext()).inflate(R.layout.myproduct_list_item2,parent,false);
                    holder=new Holder2();
                    ButterKnife.bind(holder,convertView);
                    convertView.setTag(holder);
                }
                else
                {
                    holder=(Holder2)convertView.getTag();
                }
                final ProductBean productBean=(ProductBean)mProductBeanList.get(position);
                LoadImageUtils.getInstance().loadImage(holder.headerImage, productBean.getAdvertisementPhoto(), parent.getContext());
                holder.productName.setText(productBean.getName()
                );
                holder.adverseSerialNumber.setText(productBean.getSerialNumber());
                //点击删除
                holder.deleteBn.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if(mProductListView.isScrollFinished())
                        {
                            int count=mDateCountProduct.get(productBean.getDateTime())-1;
                            if(position>=0)
                                mProductBeanList.remove(position);
                            //如果对应时间没有产品，则删除时间
                            if(count==0)
                            {
                                mProductBeanList.remove(productBean.getDateTime());
                                ThreadManager.addSingalExecutorTask(new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        //删除数据库对应的时间
                                        DataSupport.deleteAll(TimeProductBean.class, "datetime=?", productBean.getDateTime());
                                    }
                                });
                            }
                            mDateCountProduct.put(productBean.getDateTime(),count);
                            ArrayMap<String,String> params=new ArrayMap<>(2);
                            params.put(Consts.USERID_KEY, ShareUtils.getUserId());
                            params.put(Consts.SERIALNUMBERVALUEKEY, productBean.getSerialNumber());
                            NetWorkHandlerUtils.postAsynHandler(Consts.DELETE_PRODUCT, params, "删除成功", new NetWorkHandlerUtils.PostSuccessCallback<Object>()
                            {
                                @Override
                                public void success(Object result)
                                {
                                    ThreadManager.addSingalExecutorTask(new Runnable()
                                    {
                                        @Override
                                        public void run()
                                        {
                                            //删除数据库对应的产品
                                            DataSupport.deleteAll(ProductBean.class, "serialnumber=? and name=?", productBean.getSerialNumber(), productBean.getName());
                                        }
                                    });
                                }
                            });
                            notifyDataSetChanged();
                        }
                    }
                });
           }
            else if(mProductBeanList.get(position) instanceof String )
            {
                Holder1 holder;
                if(convertView==null)
                {
                    convertView=LayoutInflater.from(parent.getContext()).inflate(R.layout.myproduct_list_item1,parent,false);
                    holder=new Holder1();
                    ButterKnife.bind(holder,convertView);
                    convertView.setTag(holder);
                }
                else
                {
                    holder=(Holder1)convertView.getTag();
                }
                holder.dateTextView.setText((String)mProductBeanList.get(position));
            }
            return convertView;
        }
    }

    class Holder1
    {
        @Bind(R.id.date)
        TextView dateTextView;
    }

    class Holder2
    {
        @Bind(R.id.header_image)
        ImageView headerImage;
        @Bind(R.id.product_name)
        TextView productName;
        @Bind(R.id.adverse_serialnumber)
        TextView adverseSerialNumber;
        @Bind(R.id.item_delete)
        Button deleteBn;
    }

    @OnItemClick(R.id.product_slidelistview)
    public void onItemClick(int position)
    {
        if(mProductBeanList.get(position) instanceof ProductBean)
        {
            Log.i(TAG,"点击");
            Intent intent=new Intent();
            Bundle bundle=new Bundle();
            bundle.putParcelable(Consts.PRODUCT_BEAN,(ProductBean)mProductBeanList.get(position));
           // intent.putExtra(Consts.SERIALNUMBERVALUEKEY,((ProductBean)mProductBeanList.get(position)).getSerialNumber());
            intent.putExtras(bundle);
            intent.setClass(getActivity(), ProductDetailActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        //释放线程池资源
        ThreadManager.release();
        ButterKnife.unbind(this);
    }

}
