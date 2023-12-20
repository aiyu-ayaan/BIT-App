package com.atech.bit.ui.comman

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.atech.bit.ui.theme.BITAppTheme


@Composable
fun ChooseSemBottomSheet(
    modifier: Modifier = Modifier,

) {
    Column(
        modifier = modifier
    ) {

    }
}

@Preview(showBackground = true)
@Composable
private fun ChooseSemBottomSheetPreview() {
    BITAppTheme {
        ChooseSemBottomSheet()
    }
}