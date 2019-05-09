package com.boardgamegeek.ui

import androidx.fragment.app.Fragment
import com.boardgamegeek.R

class TopGamesActivity : TopLevelSinglePaneActivity() {
    override val answersContentType = "Top Games"

    override fun onCreatePane(): Fragment = TopGamesFragment()

    override fun getNavigationItemId() = R.id.top_games
}
