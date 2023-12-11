package com.atech.course.screen.sub_view

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.atech.components.BackToolbar
import com.atech.course.CourseViewModel
import com.atech.theme.BITAppTheme
import com.atech.utils.hexToRgb
import com.mukesh.MarkDown
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewSubjectScreen(
    modifier: Modifier = Modifier,
    viewModel: CourseViewModel = hiltViewModel(),
    navController: NavController = rememberNavController()
) {

    val toolbarScroll = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val scrollState = rememberScrollState()
    var isComposeViewVisible by rememberSaveable {
        mutableStateOf(false)
    }
    LaunchedEffect(key1 = true) {
        delay(500)
        isComposeViewVisible = true
    }
    BackHandler {
        isComposeViewVisible = false
        navController.navigateUp()
    }

    Scaffold(
        topBar = {
            BackToolbar(
                title = "",
                onNavigationClick = {
                    isComposeViewVisible = false
                    navController.navigateUp()
                },
                scrollBehavior = toolbarScroll
            )
        }
    ) {
        Column(
            modifier = modifier
                .nestedScroll(toolbarScroll.nestedScrollConnection)
                .verticalScroll(scrollState)
                .padding(it)
                .background(
                    MaterialTheme.colorScheme.surface
                )
        ) {
            if (isComposeViewVisible)
                LoadMarkDown()
        }
    }
}

val demo = "## MODERN OPERATING SYSTEMS\n" +
        "\n" +
        "### Module I:\n" +
        "`Overview of Operating Systems`: OS and the Computer System, Efficiency, System Performance and\n" +
        "User Convenience, Classes of Operating Systems, Batch Processing Systems, Multiprogramming\n" +
        "Systems, Time Sharing Systems, Real Time Operating Systems, Distributed Operating Systems, Modern\n" +
        "Operating Systems.\n" +
        "\n" +
        "### Module II:\n" +
        "`Processes and Threads:` Processes and Programs, Programmer view of Processes, OS view of\n" +
        "Processes, Threads, Case studies of Processes and Threads.\n" +
        "\n" +
        "`Scheduling`: Preliminaries, Non-preemptive Scheduling Policies, Preemptive Scheduling Policies,\n" +
        "Scheduling in Practice, Real Time Scheduling, Scheduling in Unix, Scheduling in Linux, Scheduling in\n" +
        "Windows, Performance Analysis of Scheduling Policies. \n" +
        "\n" +
        "### Module III:\n" +
        "`Memory Management:` Managing the Memory Hierarchy, Static and Dynamic Memory Allocation,\n" +
        "Memory Allocation to a Process, Reuse of Memory, Contiguous Memory Allocation, Noncontiguous\n" +
        "Memory Allocation, Paging, Segmentation, Segmentation with Paging, Kernel Memory Allocation, A\n" +
        "Review of Relocation, Linking and Program Forms.\n" +
        "\n" +
        "`Virtual Memory`: Virtual Memory Basics, Demand Paging, Page Replacement Policies, Memory\n" +
        "Allocation to a Process, Shared Pages, Memory Mapped Files, Unix Virtual Memory, Linux Virtual\n" +
        "Memory, Virtual Memory using Segmentation.\n" +
        "\n" +
        "\n" +
        "### Module IV:\n" +
        "`File Systems`: File System and IOCS, Files and File Operations, Fundamental File Organizations,\n" +
        "Directory Structures, File Protection, Interface between File System and IOCS, Allocation of Disk\n" +
        "Space, Implementing File Access, File Sharing Semantics, File System Reliability, Virtual File System,\n" +
        "Unix File System, Linux File System, Windows File System, Performance of File Systems.\n" +
        "\n" +
        "### Module V:\n" +
        "`Security and Protection`: Overview of Security and Protection, Goals of Security and Protection,\n" +
        "Security Attacks, Formal and Practical aspects of Security, Encryption, Authentication and Password\n" +
        "Security, Access Descriptors and the Access Control Matrix, Protection Structures, Capabilities, Unix\n" +
        "Security, Linux Security, Windows Security\n" +
        "\n" +
        "\n" +
        "### Books\n" +
        "\n" +
        "#### Text Books\n" +
        "Dhamdhere D.M., “Operating Systems: A Concept-Based Approach”, 2nd Edition, TMH, New Delhi,\n" +
        "2006.\n" +
        "\n" +
        "\n" +
        "#### Reference Books\n" +
        "1.SilberschatzA.,Galvin Peter B.,Greg Gagne, “Operating System Concepts”, 6th Edition, John Wiley,\n" +
        "Indian Reprint, 2003.\n" +
        "\n" +
        "2.Crowley C., “Operating Systems: A Design-Oriented Approach”, TMH, New Delhi, 2002.\n" +
        "\n" +
        "3.Deitel H.M., “Operating Systems”, 2nd Edition, Pearson Education, 2003.\n" +
        "\n" +
        "4.Tanenbaum A.S., “Operating System: Design and Implementation”, PHI, New Delhi,\n" +
        " 2002."

@Composable
fun LoadMarkDown(
    modifier: Modifier = Modifier
) {
    val d = demo + "<br> <br><style> body{background-color: ${
        MaterialTheme.colorScheme.surface.hexToRgb()
    } ; color:${MaterialTheme.colorScheme.onSurface.hexToRgb()};}</style>"
    MarkDown(
        text = d,
        modifier = Modifier.fillMaxSize()
    )
}

@Preview(showBackground = true)
@Composable
fun ViewSubjectScreenPreview() {
    BITAppTheme {
        LoadMarkDown()
    }
}

