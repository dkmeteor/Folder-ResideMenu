#Folder-ResideMenu
===========
A Special Drawer.

An extension of **[AndroidResideMenu](https://github.com/SpecialCyCi/AndroidResideMenu)**

---
##Screenshot

Please waiting for loading the images...

![Examples](/screenshot.png)
![Examples](/Folder-residemenu.gif)


## Usage
init ResideMenu: write these code in Activity onCreate()
```java
        // attach to current activity;
        resideMenu = new ResideMenu(this);
        resideMenu.setBackground(R.drawable.menu_background);
        resideMenu.attachToActivity(this);

        // create menu items;
        String titles[] = { "Home", "Gallery", "Calendar", "Settings" };
        int icon[] = { R.drawable.icon_home, R.drawable.icon_profile, R.drawable.icon_calendar, R.drawable.icon_settings };

        for (int i = 0; i < titles.length; i++){
            ResideMenuItem item = new ResideMenuItem(this, icon[i], titles[i]);
            item.setOnClickListener(this);
            resideMenu.addMenuItem(item,  ResideMenu.DIRECTION_LEFT); // or  ResideMenu.DIRECTION_RIGHT
        }
```
	*On some occasions, the slipping gesture function for locking/unlocking menu, may have conflicts with your widgets, such as viewpager. By then you can add the viewpager to ignored view, please refer to next chapter – Ignored Views.**

open/close menu
```java
resideMenu.openMenu(ResideMenu.DIRECTION_LEFT); // or ResideMenu.DIRECTION_RIGHT
resideMenu.closeMenu();
```

listen in the menu state
```java
    resideMenu.setMenuListener(menuListener);
    private ResideMenu.OnMenuListener menuListener = new ResideMenu.OnMenuListener() {
        @Override
        public void openMenu() {
            Toast.makeText(mContext, "Menu is opened!", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void closeMenu() {
            Toast.makeText(mContext, "Menu is closed!", Toast.LENGTH_SHORT).show();
        }
    };
```
##Ignored Views
On some occasions, the slipping gesture function for locking/unlocking menu, may have conflicts with your widgets such as viewpager.By then you can add the viewpager to ignored view.
```java
        // add gesture operation's ignored views
        FrameLayout ignored_view = (FrameLayout) findViewById(R.id.ignored_view);
        resideMenu.addIgnoredView(ignored_view);
```

So that in ignored view’s workplace, the slipping gesture will not be allowed to operate menu.

---

##Issue

If you use a GridView/ListView/ScrollView, open/close menu may cause focus change,for example, GridView may auto scroll to top.
If you don't want this,check the code in Sample->GalleryFragment

```java
        mGridView.setFocusable(false);
        view.setFocusable(false);
```

---

##Thanks to

[@specialcyci](https://github.com/SpecialCyCi)
[AndroidResideMenu](https://github.com/SpecialCyCi/AndroidResideMenu)

---


#License

    Copyright 2015 Dean Ding

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

---
Developed By


Dean <93440331@qq.com>  

Weibo：http://weibo.com/u/2699012760

![](https://avatars0.githubusercontent.com/u/5019523?v=3&s=160)
