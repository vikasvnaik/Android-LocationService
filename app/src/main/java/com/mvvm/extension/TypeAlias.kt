package com.mvvm.extension

import androidx.fragment.app.Fragment
import com.mvvm.R

typealias AppDrawable = R.drawable
typealias AppMipmap = R.mipmap
typealias AppString = R.string
typealias AppDimen = R.dimen
typealias DefaultDrawable = android.R.drawable
typealias DefaultAttr = android.R.attr
typealias AppLayout = R.layout
typealias AppColor = R.color
typealias DefaultColor = android.R.color
typealias ItemClickListener<T> = (position: Int, data: T) -> Unit
typealias ItemLongClickListener<T> = (position: Int, data: T) -> Unit
typealias ItemViewClickListener<T> = (id: Int, position: Int, data: T) -> Unit
typealias PagerFragment = Pair<Int, Fragment>
typealias Predicate<T> = (T) -> Boolean
typealias Id = R.id
//typealias AppNavigation = R.navigation
typealias AppRaw = R.raw
//
// typealias AppMenu = R.menu
typealias AppStyle = R.style
