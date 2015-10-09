package com.scau.beyondboy.idgoods.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scau.beyondboy.idgoods.MainActivity;
import com.scau.beyondboy.idgoods.MyApplication;
import com.scau.beyondboy.idgoods.ProductDetailActivity;
import com.scau.beyondboy.idgoods.R;
import com.scau.beyondboy.idgoods.consts.Consts;
import com.scau.beyondboy.idgoods.manager.ThreadManager;
import com.scau.beyondboy.idgoods.model.ProductBean;
import com.scau.beyondboy.idgoods.model.ResponseObject;
import com.scau.beyondboy.idgoods.model.TimeProductBean;
import com.scau.beyondboy.idgoods.utils.LoadImageUtils;
import com.scau.beyondboy.idgoods.utils.OkHttpNetWorkUtil;
import com.scau.beyondboy.idgoods.view.SlideListView;
import com.squareup.okhttp.Request;

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
    private MainActivity mMainActivity;
    @Bind(R.id.product_slidelistview)
    SlideListView mProductListView;
    private List<Object> mProductBeanList=new LinkedList<>();
    /**保存时间对应的产品个数*/
    private Map<String,Integer> mDateCountProduct=new LinkedHashMap<>();
    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        mMainActivity=(MainActivity)context;
        ThreadManager.scoolPoolSize=3;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view=inflater.inflate(R.layout.activity_myproduct,container,false);
        ButterKnife.bind(this, view);
        mMainActivity.mSearchView.setVisibility(View.VISIBLE);
        loadDate();
        return view;
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mMainActivity.mSearchView.setVisibility(View.GONE);
    }

    /**解析json*/
    private List<TimeProductBean> parseProductDataJson(String productDataJson)
    {
        Gson gson=new Gson();
        ResponseObject<List<TimeProductBean>> responseObject=gson.fromJson(productDataJson, new TypeToken<ResponseObject<List<TimeProductBean>>>(){}.getType());
        return responseObject.getData();
    }

    /**
     * 加载数据
     */
    private void loadDate()
    {
        OkHttpNetWorkUtil.postAsyn(Consts.GET_PRODUCT, new OkHttpNetWorkUtil.ResultCallback<String>()
        {

            @Override
            public void onError(Request request, Exception e)
            {
                Toast.makeText(getContext(),request.body().toString(),Toast.LENGTH_SHORT).show();
                Log.e(TAG, "错误", e);
            }

            @Override
            public void onResponse(String response)
            {
                List<TimeProductBean> timeProductBeanList = parseProductDataJson(response);
                for (TimeProductBean timeProductBean : timeProductBeanList)
                {
                    mProductBeanList.add(timeProductBean.getDateTime());
                    for(ProductBean productBean:timeProductBean.getBeanList())
                    {
                        productBean.setDateTime(timeProductBean.getDateTime());
                        mProductBeanList.add(productBean);
                    }
                    mDateCountProduct.put(timeProductBean.getDateTime(),timeProductBean.getBeanList().size());
                }
                Log.i(TAG, "数据：  " + mProductBeanList.size());
                mProductListView.setAdapter(new ProductAdapter());
            }
        }, new OkHttpNetWorkUtil.Param(Consts.USERID_KEY, Consts.TESTUSERID));
    }

    private class ProductAdapter extends ArrayAdapter<Object>
    {
        public ProductAdapter()
        {
            super(getActivity(),0,mProductBeanList);
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
                            Log.i(TAG, "位置：  " + position);
                            int count=mDateCountProduct.get(productBean.getDateTime())-1;
                            if(position>=0)
                                mProductBeanList.remove(position);
                            //如果对应时间没有产品，则删除时间
                            if(count==0)
                            {
                                mProductBeanList.remove(productBean.getDateTime());
                            }
                            mDateCountProduct.put(productBean.getDateTime(),count);
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
            intent.putExtra(Consts.SERIALNUMBERVALUEKEY,((ProductBean)mProductBeanList.get(position)).getSerialNumber());
            intent.setClass(getActivity(), ProductDetailActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        //释放线程池资源
        MyApplication.getInstance().sThreadManager.release();
    }
}
