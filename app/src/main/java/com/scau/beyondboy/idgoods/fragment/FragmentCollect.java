package com.scau.beyondboy.idgoods.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.scau.beyondboy.idgoods.ListenBlessActivity;
import com.scau.beyondboy.idgoods.R;
import com.scau.beyondboy.idgoods.consts.Consts;
import com.scau.beyondboy.idgoods.manager.ThreadManager;
import com.scau.beyondboy.idgoods.model.CollectBean;
import com.scau.beyondboy.idgoods.model.TimeCollectBean;
import com.scau.beyondboy.idgoods.model.TimeProductBean;
import com.scau.beyondboy.idgoods.utils.LoadImageUtils;
import com.scau.beyondboy.idgoods.utils.NetWorkHandlerUtils;
import com.scau.beyondboy.idgoods.utils.NetworkUtils;
import com.scau.beyondboy.idgoods.utils.ShareUtils;
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
 * Date: 2015-10-21
 * Time: 00:28
 * 收藏列表
 */
public class FragmentCollect extends Fragment
{
   // private static final String TAG = FragmentCollect.class.getName();
    //private MainActivity mMainActivity;
    @Bind(R.id.collect_slidelistview)
    SlideListView mCollectListView;
    private List<Object> mCollectBeanList =new LinkedList<>();
    /**保存时间对应的收藏个数*/
    private Map<String,Integer> mDateCountCollect =new LinkedHashMap<>();
    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
       // mMainActivity=(MainActivity)context;
        ThreadManager.scoolPoolSize=3;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view=inflater.inflate(R.layout.mycollect,container,false);
        ButterKnife.bind(this, view);
        //mMainActivity.mSearchView.setVisibility(View.VISIBLE);
        return view;
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
       // mMainActivity.mSearchView.setVisibility(View.GONE);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        loadDate();
        super.onViewCreated(view, savedInstanceState);
    }

    /**
     * 加载数据
     */
    private void loadDate()
    {
        if(NetworkUtils.isNetworkReachable())
        {
            ArrayMap<String,String> params=new ArrayMap<>(1);
            params.put(Consts.CUSTOMERID_KEY, ShareUtils.getUserId());
            NetWorkHandlerUtils.<TimeCollectBean>postAsynHandler(Consts.GET_COLLECT, params, null, null, new NetWorkHandlerUtils.PostCallback<List<TimeCollectBean>>()
            {
                @Override
                public void success(final List<TimeCollectBean> result)
                {
                    ThreadManager.addSingalExecutorTask(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            loadNetWordData(result);
                        }
                    });
                }
            }, new TypeToken<List<TimeCollectBean>>(){}.getType());
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
        List<TimeCollectBean> timeCollectBeanList= DataSupport.findAll(TimeCollectBean.class, true);
        for (TimeCollectBean timeCollectBean : timeCollectBeanList)
        {
            String dateTimedb=timeCollectBean.getDateTime();
            String[] dateArray=dateTimedb.split("/");
            if(dateArray.length != 0)
            {
                dateTimedb=dateArray[0]+"年"+dateArray[1]+"月"+dateArray[2]+"日";
            }
            mCollectBeanList.add(dateTimedb);
            for(CollectBean collectBean:timeCollectBean.getBeanList())
            {
                collectBean.setDateTime(dateTimedb);
                mCollectBeanList.add(collectBean);
            }
            mDateCountCollect.put(dateTimedb, timeCollectBean.getBeanList().size());
        }
        getActivity().runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                mCollectListView.setAdapter(new CollectAdapter());
            }
        });
    }

    private void loadNetWordData(List<TimeCollectBean> result)
    {
        for (TimeCollectBean timeCollecttBean : result)
        {
            //存入数据库时间，由于数据库识别不了中文，转成全英的
            String dateTimedb = timeCollecttBean.getDateTime().replaceAll("[\u4e00-\u9fa5]+", "/");
            dateTimedb = dateTimedb.substring(0, dateTimedb.length() - 1);
            TimeCollectBean timeCollectBeandb;
            List<TimeCollectBean> timeCollectBeanListdb = DataSupport.where("datetime =?", dateTimedb).find(TimeCollectBean.class);
            if (timeCollectBeanListdb == null || timeCollectBeanListdb.size() == 0)
            {
                timeCollectBeandb = new TimeCollectBean();
                //保存到数据库
                timeCollectBeandb.setDateTime(dateTimedb);
            } else
            {
                //返回原来数据
                timeCollectBeandb = DataSupport.where("datetime =?", dateTimedb).find(TimeCollectBean.class).get(0);
            }
            mCollectBeanList.add(timeCollecttBean.getDateTime());
            for (CollectBean collectBean : timeCollecttBean.getBeanList())
            {
                collectBean.setDateTime(timeCollecttBean.getDateTime());
                //添加数据库中
                if (DataSupport.where("serialnumbervalue=? and name=?", collectBean.getSerialNumberValue(), collectBean.getName()).find(CollectBean.class).size() == 0)
                {
                    collectBean.save();
                }
                //添加不重复到数据库中
                timeCollectBeandb.getBeanList().add(collectBean);
                mCollectBeanList.add(collectBean);
            }
            //提交到数据库中
            timeCollectBeandb.save();
            mDateCountCollect.put(timeCollecttBean.getDateTime(), timeCollecttBean.getBeanList().size());
        }
        getActivity().runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                mCollectListView.setAdapter(new CollectAdapter());
            }
        });
    }

    private class CollectAdapter extends ArrayAdapter<Object>
    {
        public CollectAdapter()
        {
            super(getActivity(),0, mCollectBeanList);
        }

        @Override
        public int getViewTypeCount()
        {
            return 2;
        }

        @Override
        public Object getItem(int position)
        {
            return mCollectBeanList.get(position);
        }

        @Override
        public int getItemViewType(int position)
        {
            if(getItem(position) instanceof CollectBean)
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
            if(mCollectBeanList.get(position) instanceof CollectBean)
            {
                Holder2 holder;
                if(convertView==null)
                {
                    convertView=LayoutInflater.from(parent.getContext()).inflate(R.layout.mycollect_list_item2,parent,false);
                    holder=new Holder2();
                    ButterKnife.bind(holder,convertView);
                    convertView.setTag(holder);
                }
                else
                {
                    holder=(Holder2)convertView.getTag();
                }
                final CollectBean collectBean=(CollectBean) mCollectBeanList.get(position);
                LoadImageUtils.getInstance().loadImage(holder.headerImage, collectBean.getAdvertisementPhoto(), parent.getContext());
                holder.collectName.setText(collectBean.getName()
                );
                holder.adverseSerialNumber.setText(collectBean.getSerialNumberValue());
                //点击删除
                holder.deleteBn.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if(mCollectListView.isScrollFinished())
                        {
                            int count= mDateCountCollect.get(collectBean.getDateTime())-1;
                            if(position>=0)
                                mCollectBeanList.remove(position);
                            //如果对应时间没有产品，则删除时间
                            if(count==0)
                            {
                                mCollectBeanList.remove(collectBean.getDateTime());
                                ThreadManager.addSingalExecutorTask(new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        //删除数据库对应的时间
                                        DataSupport.deleteAll(TimeProductBean.class, "datetime=?", collectBean.getDateTime());
                                    }
                                });

                            }
                            mDateCountCollect.put(collectBean.getDateTime(), count);
                            ArrayMap<String,String> params=new ArrayMap<>(2);
                            params.put(Consts.CUSTOMERID_KEY, ShareUtils.getUserId());
                            params.put(Consts.SERIALNUMBERVALUEKEY, collectBean.getSerialNumberValue());
                            NetWorkHandlerUtils.postAsynHandler(Consts.DELETE_COLLECT, params, "删除成功", new NetWorkHandlerUtils.PostCallback<Object>()
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
                                            DataSupport.deleteAll(CollectBean.class, "serialnumber=? and name=?", collectBean.getSerialNumberValue(), collectBean.getName());
                                        }
                                    });

                                }
                            });
                            notifyDataSetChanged();
                        }
                    }
                });
            }
            else if(mCollectBeanList.get(position) instanceof String )
            {
                Holder1 holder;
                if(convertView==null)
                {
                    convertView=LayoutInflater.from(parent.getContext()).inflate(R.layout.mycollect_list_item1,parent,false);
                    holder=new Holder1();
                    ButterKnife.bind(holder,convertView);
                    convertView.setTag(holder);
                }
                else
                {
                    holder=(Holder1)convertView.getTag();
                }
                holder.dateTextView.setText((String) mCollectBeanList.get(position));
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
        @Bind(R.id.collect_name)
        TextView collectName;
        @Bind(R.id.adverse_serialnumber)
        TextView adverseSerialNumber;
        @Bind(R.id.item_delete)
        Button deleteBn;
    }


    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        //释放线程池资源
        ThreadManager.release();
    }


    @OnItemClick(R.id.collect_slidelistview)
    public void onItemClick(int position)
    {
        if(mCollectBeanList.get(position) instanceof CollectBean)
        {
            Intent intent=new Intent();
            Bundle bundle=new Bundle();
            bundle.putParcelable(Consts.COLLECT_BEAN,(CollectBean)mCollectBeanList.get(position));
            bundle.putBoolean(Consts.FRAGMENT_COLLECT, true);
            // intent.putExtra(Consts.SERIALNUMBERVALUEKEY,((ProductBean)mProductBeanList.get(position)).getSerialNumber());
            intent.putExtras(bundle);
            intent.setClass(getActivity(), ListenBlessActivity.class);
            startActivity(intent);
        }
    }
}
