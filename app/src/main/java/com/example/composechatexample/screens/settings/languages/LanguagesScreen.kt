package com.example.composechatexample.screens.settings.languages

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.composechatexample.R
import com.example.composechatexample.screens.settings.languages.model.LanguagesScreenEvent
import com.example.composechatexample.utils.Constants
import com.example.composechatexample.utils.Ext.setLanguage
import kotlinx.coroutines.flow.collectLatest

@Composable
fun LanguageScreen(
    navController: NavHostController,
) {

    val context = LocalContext.current
    val viewModel: LanguagesViewModel = hiltViewModel()
    val uiState = viewModel.uiState.collectAsState()

    ConstraintLayout(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
            .onGloballyPositioned {
            },
    ) {
        val (
            languagesList
        ) = createRefs()
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(languagesList) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    height = Dimension.fillToConstraints
                },
        ) {
            items(uiState.value.languages) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable {
                            viewModel.onLanguageClick(it)
                        },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = it.languageValue,
                        fontSize = 16.sp,
                    )
                    Image(
                        painter = painterResource(id = R.drawable.ic_arrow_right),
                        contentDescription = Constants.CONTENT_DESCRIPTION
                    )
                }
            }
        }
    }
    LaunchedEffect(key1 = Unit) {
        viewModel.eventsFlow.collectLatest { value ->
            when (value) {
                is LanguagesScreenEvent.SetLanguage -> {
                    setLanguage(context = context, language = value.language.languageCode)
                    navController.navigateUp()
                }
            }
        }
    }
}