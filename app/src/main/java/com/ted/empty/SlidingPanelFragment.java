package com.ted.empty;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Copyright (C) 2008 The Android Open Source Project
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p/>
 * Created by Ted.Yt on 9/12/16.
 */
public class SlidingPanelFragment extends Fragment {

    private ListView mMenus;
    private String[] mMenuItmeStr = {"Bear","Bird","Cat","Tigers","Panda"};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_letf_menu,container,false);

        mMenus = (ListView) view.findViewById(R.id.id_left_menu_lv);
        mMenus.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.item_left_menu, mMenuItmeStr));
        return view;
    }
}
