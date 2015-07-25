package org.zarroboogs.maps.ui;

/**
 * Created by wangdiyuan on 15-7-24.
 */
public class SearchMapsPresenter {


    private ISearchMapsView mSearchMapsView;

    public SearchMapsPresenter(ISearchMapsView searchMapsView){
        this.mSearchMapsView = searchMapsView;
    }

    public void openDrawer(){
        mSearchMapsView.openDrawer();
    }
    public void closeDrawer(){
        mSearchMapsView.closeDrawer();
    }

    public void enterSearch(){
        mSearchMapsView.enterSearch();
    }
    public void exitSearch(){
        mSearchMapsView.exitSearch();
    }

}
