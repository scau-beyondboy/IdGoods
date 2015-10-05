package com.scau.beyondboy.idgoods.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scau.beyondboy.idgoods.MainActivity;
import com.scau.beyondboy.idgoods.ProductDetailActivity;
import com.scau.beyondboy.idgoods.R;
import com.scau.beyondboy.idgoods.consts.Consts;
import com.scau.beyondboy.idgoods.model.ProductBean;
import com.scau.beyondboy.idgoods.model.ResponseObject;
import com.scau.beyondboy.idgoods.model.TimeProductBean;
import com.scau.beyondboy.idgoods.utils.LoadImageUtils;
import com.scau.beyondboy.idgoods.utils.OkHttpNetWorkUtil;
import com.scau.beyondboy.idgoods.view.SlideListView;
import com.squareup.okhttp.Request;

import java.util.LinkedList;
import java.util.List;

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
    private List<ProductBean> mProductBeanList=new LinkedList<>();
    private SparseArray<String> mDateMap=new SparseArray<>();
    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        mMainActivity=(MainActivity)context;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view=inflater.inflate(R.layout.myproduct,container,false);
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
                List<TimeProductBean> timeProductBeanList =parseProductDataJson(response);
                Log.i(TAG,"输出结果：  "+mProductBeanList.size());
                int dateKey = 0;
                for (TimeProductBean timeProductBean : timeProductBeanList)
                {
                    mProductBeanList.addAll(mProductBeanList.size(), timeProductBean.getBeanList());
                    mDateMap.put(dateKey, timeProductBean.getDateTime());
                    dateKey += timeProductBean.getBeanList().size();
                }
                Log.i(TAG, "数据：  " + mProductBeanList.size());
                mProductListView.setAdapter(new ProductAdapter());
            }
        }, new OkHttpNetWorkUtil.Param(Consts.USERID_KEY, Consts.TESTUSERID));
    }

    private class ProductAdapter extends ArrayAdapter<ProductBean>
    {
        public ProductAdapter()
        {
            super(getActivity(),0,mProductBeanList);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent)
        {
            int marginTop;
            Holder holder;
            if(convertView==null)
            {
                convertView=LayoutInflater.from(parent.getContext()).inflate(R.layout.myproduct_list_item,parent,false);
                holder=new Holder();
                ButterKnife.bind(holder,convertView);
                convertView.setTag(holder);
            }
            else
            {
                holder=(Holder)convertView.getTag();
            }
            if(mDateMap.get(position)!=null)
            {
                holder.dateTextView.setVisibility(View.VISIBLE);
                holder.dateTextView.setText(mDateMap.get(position));
                holder.dateTextView.measure(0,0);
                marginTop=holder.dateTextView.getMeasuredHeight();
                Log.i(TAG,"高度：  "+marginTop);
                LinearLayout.LayoutParams layoutParams=(LinearLayout.LayoutParams)holder.deleteBn.getLayoutParams();
                layoutParams.topMargin=marginTop;
                holder.deleteBn.setLayoutParams(layoutParams);
            }
            ProductBean productBean=mProductBeanList.get(position);
            LoadImageUtils.getInstance().loadImage(holder.headerImage, productBean.getAdvertisementPhoto(), parent.getContext());
            //OkHttpNetWorkUtil.displayImage(holder.headerImage, productBean.getAdvertisementPhoto());
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
                        mProductBeanList.remove(position);
                        notifyDataSetChanged();
                    }
                }
            });
            return convertView;
        }
    }

    class Holder
    {
        @Bind(R.id.date)
        TextView dateTextView;
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
        Log.i(TAG,"点击");
        Intent intent=new Intent();
        intent.putExtra(Consts.SERIALNUMBERVALUEKEY,mProductBeanList.get(position).getSerialNumber());
        intent.setClass(getActivity(), ProductDetailActivity.class);
        startActivity(intent);
    }

}
