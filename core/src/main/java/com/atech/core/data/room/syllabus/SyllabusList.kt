/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 9/1/21, 1:09 PM
 * Copyright (c) 2021 . All rights reserved.
 * Last modified 9/1/21, 11:14 AM
 */



package com.atech.core.data.room.syllabus

object SyllabusList {
    val syllabus = listOf(
        //                BCA
//                SEM1
        SyllabusModel(
            subject = "Problem Solving And Programming in C",
            code = "CA101",
            credits = 3,
            openCode = "BCA11",
            shortName = "c lang pspc",
            listOrder = 1
        ),
        SyllabusModel(
            subject = "Logical Organizations of Computers",
            code = "CA103",
            credits = 4,
            openCode = "BCA12", shortName = "loc",
            listOrder = 2
        ),
        SyllabusModel(
            subject = "Business Communications",
            code = "MT123",
            credits = 2,
            openCode = "BCA13",
            shortName = "bs1",
            listOrder = 3
        ),
        SyllabusModel(
            subject = "Environmental Science", code = "CE101", 2, openCode = "BCA14",
            shortName = "es", listOrder = 4
        ),
        SyllabusModel(
            subject = "Mathematics 1", code = "CA104", 3, openCode = "BCA15",
            shortName = "maths1", listOrder = 5
        ),
        SyllabusModel(
            subject = "Problem Solving And Programming in C Lab",
            code = "CA102",
            credits = 2,
            openCode = "BCA16",
            type = "LAB",
            shortName = "pspc lab", listOrder = 6
        ),
        SyllabusModel(
            "Office Automation Tools", "CA180", 2, "BCA17", "LAB",
            shortName = "os lab", listOrder = 7
        ),
        //SEM2

        SyllabusModel(
            "Data Structures",
            "CA155",
            4,
            "BCA21",
            shortName = "ds",
            listOrder = 1
        ),

        SyllabusModel(
            "Discrete Structures",
            "CA157",
            3,
            "BCA22",
            shortName = "ds",
            listOrder = 2
        ),
        SyllabusModel(
            "Operating System Concepts",
            "CA160",
            3,
            "BCA23",
            shortName = "os",
            listOrder = 3
        ),
        SyllabusModel(
            "Numerical and Statistical Methods",
            "CA158",
            3,
            "BCA24",
            shortName = "nsm",
            listOrder = 4
        ),
        SyllabusModel(
            "Data Structures Lab",
            "CA156",
            2,
            "BCA25",
            "LAB",
            shortName = "ds lab",
            listOrder = 5
        ),
        SyllabusModel(
            "Operating System Lab",
            "CA161",
            2,
            "BCA26",
            "LAB",
            shortName = "os lab",
            listOrder = 6
        ),
        SyllabusModel(
            "Numerical and Statistical Methods Lab",
            "CA159",
            2,
            "BCA27",
            "LAB", shortName = "nsm lab", listOrder = 7
        ),
        SyllabusModel(
            "HTML Programming Lab",
            "CA280",
            3,
            "BCA28",
            "LAB",
            shortName = "html lab",
            listOrder = 8
        ),
//               SEM 3

        SyllabusModel(
            "OOP in Java",
            "CA201",
            3,
            "BCA31",
            shortName = "java",
            listOrder = 1
        ),
        SyllabusModel(
            "Database Management Systems",
            "CA203",
            4,
            "BCA32",
            shortName = "dbms",
            listOrder = 2
        ),

        SyllabusModel(
            "OOP in Java Lab",
            "CA202",
            2,
            "BCA33",
            "LAB",
            shortName = "java lab",
            listOrder = 3
        ),

        SyllabusModel(
            "DBMS Lab",
            "CA204",
            2,
            "BCA34",
            "LAB",
            shortName = "dbms lab",
            listOrder = 4
        ),

        SyllabusModel(
            "Internet Technologies",
            "CA202",
            4,
            "BCA35",
            "PE",
            shortName = "it",
            listOrder = 5
        ),

        SyllabusModel(
            "Internet Technologies Lab ",
            "CA221",
            2,
            "BCA36",
            "PE",
            shortName = "it lab",
            listOrder = 6
        ),

        SyllabusModel(
            "Programming in Visual Basic",
            "CA281",
            2,
            "BCA37",
            "PE",
            shortName = "vb",
            listOrder = 7
        ),
        SyllabusModel(
            "Mobile Application",
            "CA222",
            4,
            "BCA38",
            "PE",
            shortName = "ma",
            listOrder = 8
        ),
//                SEM 4

        SyllabusModel(
            "Fundamentals of Computer Algorithms",
            "CA255",
            4,
            "BCA41", shortName = "algo", listOrder = 1
        ),

        SyllabusModel(
            "Web Programming",
            "CA256",
            3,
            "BCA42",
            shortName = "wp",
            listOrder = 2
        ),

        SyllabusModel(
            "Software Engineering",
            "CA258",
            3,
            "BCA43",
            shortName = "se",
            listOrder = 3
        ),

        SyllabusModel(
            "Web Programming Lab",
            "CA257",
            2,
            "BCA44",
            "LAB",
            shortName = "wp lab",
            listOrder = 4
        ),
        SyllabusModel(
            "Software Engineering Lab",
            "CA259",
            2,
            "BCA45",
            "LAB",
            shortName = "se lab",
            listOrder = 5
        ),
        SyllabusModel(
            "Cloud Computing",
            "CA244",
            4,
            "BCA46",
            "PE",
            shortName = "cc",
            listOrder = 6
        ),

        SyllabusModel(
            "Data Analytics",
            "CA272",
            4,
            "BCA47",
            "PE",
            shortName = "da",
            listOrder = 7
        ),

        SyllabusModel(
            "Data Analytics Lab",
            "CA274",
            2,
            "BCA48",
            "PE",
            shortName = "da lab",
            listOrder = 8
        ),
        SyllabusModel(
            "E-Commerce Technology",
            "CA223",
            4,
            "BCA49",
            "PE",
            shortName = "ecom",
            listOrder = 9
        ),

        SyllabusModel(
            "Soft Computing and Applications",
            "CA271",
            4,
            "BCA410",
            "PE", shortName = "sc", listOrder = 10
        ),
        SyllabusModel(
            "Soft Computing and Applications Lab",
            "CA272",
            2,
            "BCA411",
            "PE", shortName = "sc lab", listOrder = 11
        ),
//                SEM 5

        SyllabusModel(
            "Computer Graphics and Multimedia",
            "CA301",
            3,
            "BCA51",
            shortName = "cgm",
            listOrder = 1
        ),

        SyllabusModel(
            "Computer Networks",
            "CA303",
            3,
            "BCA52",
            shortName = "cn",
            listOrder = 2
        ),

        SyllabusModel(
            "Management Information Systems",
            "CA304",
            3,
            "BCA53",
            shortName = "mis",
            listOrder = 3
        ),

        SyllabusModel(
            "Computer Graphics Lab",
            "CA302",
            2,
            "BCA54",
            "LAB",
            shortName = "cg lab",
            listOrder = 4
        ),
        SyllabusModel(
            "Android Programming",
            "CA275",
            4,
            "BCA55",
            "PE",
            shortName = "ap android",
            listOrder = 5
        ),

        SyllabusModel(
            "Android Programming Lab",
            "CA276",
            2,
            "BCA56",
            "PE",
            shortName = "ap android lab",
            listOrder = 6
        ),

        SyllabusModel(
            "Network Security",
            "CA322",
            3,
            "BCA57",
            "PE",
            shortName = "ns network",
            listOrder = 7
        ),

        SyllabusModel(
            "Python Programming",
            "CA277",
            4,
            "BCA58",
            "PE",
            shortName = "python",
            listOrder = 8
        ),

        SyllabusModel(
            "Python Programming Lab",
            "CA278",
            2,
            "BCA59",
            "PE",
            shortName = "python lab",
            listOrder = 9
        ),

        SyllabusModel(
            "Software Testing",
            "CA320",
            3,
            "BCA510",
            "PE",
            shortName = "st",
            listOrder = 10
        ),

        SyllabusModel(
            "Software Test Lab",
            "CA321",
            2,
            "BCA511",
            "PE",
            shortName = "st lab",
            listOrder = 11
        ),
//                SEM 6

        SyllabusModel(
            "Data Mining",
            "CA355",
            3,
            "BCA61",
            shortName = "dm",
            listOrder = 1
        ),

        SyllabusModel(
            "Distributed Computing",
            "CA356",
            3,
            "BCA62",
            shortName = "dc",
            listOrder = 2
        ),

        SyllabusModel(
            "Cyber Forensics",
            "CA232",
            4,
            "BCA63",
            "PE",
            shortName = "cf",
            listOrder = 3
        ),

        SyllabusModel(
            "Unix and Shell Programming",
            "CA325",
            4,
            "BCA64",
            "PE",
            shortName = "unix usp",
            listOrder = 4
        ),
        SyllabusModel(
            "Unix and Shell Programming Lab",
            "CA326",
            2,
            "BCA65",
            "PE", shortName = "unix usp lab", listOrder = 6
        ),

        SyllabusModel(
            "System Programming",
            "CA278",
            4,
            "BCA66",
            "PE",
            shortName = "sp",
            listOrder = 7
        ),

        SyllabusModel(
            "Decision Support System",
            "CA331",
            4,
            "BCA67",
            "PE",
            shortName = "dss",
            listOrder = 8
        ),

        SyllabusModel(
            "Distributed Database System",
            "CA328",
            4,
            "BCA68",
            "PE",
            shortName = "dbs",
            listOrder = 9
        ),

//                BBA
//                SEM 1

        SyllabusModel(
            "General Principles of Management",
            "MT101",
            3,
            "BBA11",
            shortName = "gpm",
            listOrder = 1
        ),

        SyllabusModel(
            "Business Statistics",
            "MT102",
            4,
            "BBA12",
            shortName = "bs",
            listOrder = 2
        ),

        SyllabusModel(
            "Introduction To Business Accounting",
            "MT103",
            3,
            "BBA13", shortName = "iba account", listOrder = 3
        ),
        SyllabusModel(
            "Computerised Accounting Lab",
            "MT104",
            2,
            "BBA14",
            shortName = "ca lab",
            listOrder = 4
        ),

        SyllabusModel(
            "Business Communication",
            "MT105",
            2,
            "BBA15",
            shortName = "bc 1",
            listOrder = 6
        ),

        SyllabusModel(
            "Fundamentals of Computing",
            "MT106",
            4,
            "BBA16",
            shortName = "fc",
            listOrder = 7
        ),

//                SEM 2

        SyllabusModel(
            "Organisational Behaviour",
            "MT107",
            3,
            "BBA21",
            shortName = "ob",
            listOrder = 1
        ),

        SyllabusModel(
            "Quantitative Techniques in Management",
            "MT108",
            4,
            "BBA22", shortName = "qtm", listOrder = 2
        ),

        SyllabusModel(
            "Principles of Marketing-1",
            "MT109",
            3,
            "BBA23",
            shortName = "pm 1",
            listOrder = 3
        ),

        SyllabusModel(
            "Business Communication – II",
            "MT110",
            2,
            "BBA24",
            shortName = "bc 2",
            listOrder = 4
        ),
        SyllabusModel(
            "Introduction to Materials Management and Production Management",
            "MT111",
            3,
            "BBA25", shortName = "immpm", listOrder = 6
        ),

        SyllabusModel(
            "Business Economics",
            "MT112",
            3,
            "BBA26",
            shortName = "be",
            listOrder = 7
        ),

        SyllabusModel(
            "Basics of Financial Management",
            "MT113",
            3,
            "BBA27",
            shortName = "bfm",
            listOrder = 8
        ),

//                SEM 3

        SyllabusModel(
            "Human Resource Management",
            "MT201",
            3,
            "BBA31",
            shortName = "hrm",
            listOrder = 1
        ),

        SyllabusModel(
            "Legal Aspects of Management",
            "MT202",
            3,
            "BBA32",
            shortName = "lam",
            listOrder = 2
        ),

        SyllabusModel(
            "Introduction to Indian Financial System",
            "MT203",
            3,
            "BBA33", shortName = "iifs financial", listOrder = 3
        ),

        SyllabusModel(
            "Constitution of India",
            "MT204",
            2,
            "BBA34",
            shortName = "ci con",
            listOrder = 4
        ),

        SyllabusModel(
            "Principles of Marketing- II",
            "MT205",
            3,
            "BBA35",
            shortName = "pm 2",
            listOrder = 5
        ),

        SyllabusModel(
            "E-Commerce",
            "MT206",
            2,
            "BBA36",
            shortName = "ec",
            listOrder = 6
        ),

        SyllabusModel(
            "Data Analysis for Decision Making",
            "MT207",
            2,
            "BBA37",
            shortName = "dadm",
            listOrder = 7
        ),

        SyllabusModel(
            "Research Methodology",
            "MT208",
            3,
            "BBA38",
            shortName = "rm",
            listOrder = 8
        ),

//                SEM 4

        SyllabusModel(
            "Management and Control of Cost",
            "MT209",
            3,
            "BBA41",
            shortName = "mcc",
            listOrder = 1
        ),
        SyllabusModel(
            "Fundamentals of Operations Research",
            "MT210",
            4,
            "BBA42", shortName = "for", listOrder = 2
        ),

        SyllabusModel(
            "Sales and Distribution Management",
            "MT211",
            3,
            "BBA43",
            shortName = "sdm",
            listOrder = 3
        ),

        SyllabusModel(
            "Project Management",
            "MT212",
            3,
            "BBA44",
            shortName = "pm",
            listOrder = 4
        ),
        SyllabusModel(
            "Web Applications of Business",
            "MT213",
            2,
            "BBA45",
            shortName = "wab",
            listOrder = 5
        ),

        SyllabusModel(
            "Management Information System",
            "MT214",
            3,
            "BBA46",
            shortName = "mis",
            listOrder = 6
        ),

        SyllabusModel(
            "Entrepreneurship and Small Business",
            "MT215",
            2,
            "BBA47", shortName = "esb", listOrder = 6
        ),
//                SEM 5

        SyllabusModel(
            "Business Ethics",
            "MT301",
            3,
            "BBA51",
            shortName = "be",
            listOrder = 1
        ),

        SyllabusModel(
            "Introduction On Sustainable Development",
            "MT302",
            2,
            "BBA52", shortName = "isd", listOrder = 2
        ),

        SyllabusModel(
            "Banking Concepts and Practices",
            code = "MT 307",
            credits = 3,
            openCode = "BBA53",
            type = "PE",
            group = "FINANCE GROUP",
            shortName = "bcp",
            listOrder = 3
        ),

        SyllabusModel(
            "International Finance",
            code = " MT308",
            credits = 3,
            openCode = "BBA54",
            type = "PE",
            group = "FINANCE GROUP",
            shortName = "if",
            listOrder = 4
        ),
        SyllabusModel(
            "Training & Development",
            code = "MT323",
            credits = 3,
            openCode = "BBA55",
            type = "PE",
            group = "HUMAN RESOURCE GROUP",
            shortName = "td",
            listOrder = 5
        ),
        SyllabusModel(
            "Manpower Planning",
            code = "MT325",
            credits = 3,
            openCode = "BBA56",
            type = "PE",
            group = "HUMAN RESOURCE GROUP",
            shortName = "mp",
            listOrder = 6
        ),
        SyllabusModel(
            "International Marketing",
            code = "MT316",
            credits = 3,
            openCode = "BBA57",
            type = "PE",
            group = "MARKETING GROUP",
            shortName = "im inte",
            listOrder = 7
        ),
        SyllabusModel(
            "Retail Management",
            code = "MT318",
            credits = 3,
            openCode = "BBA58",
            type = "PE",
            group = "MARKETING GROUP",
            shortName = "rm retail",
            listOrder = 8
        ),
        SyllabusModel(
            "Services Marketing",
            code = "MT317",
            credits = 3,
            openCode = "BBA59",
            type = "PE",
            group = "MARKETING GROUP",
            shortName = "ds servi",
            listOrder = 9
        ),

//        TODO ADD HERE
//                SEM 6

        SyllabusModel(
            "Strategic Management",
            "MT304",
            3,
            "BBA61",
            shortName = "sm",
            listOrder = 1
        ),

        SyllabusModel(
            "Corporate Taxation",
            code = "MT306",
            credits = 3,
            openCode = "BBA62",
            type = "PE",
            group = "FINANCE GROUP",
            shortName = "ct",
            listOrder = 2
        ),

//        TODO REMOVE FROM HERE

        SyllabusModel(
            "Equity and Debt Market",
            code = "MT309",
            credits = 3,
            openCode = "BBA65",
            type = "PE",
            group = "FINANCE GROUP",
            shortName = "edm",
            listOrder = 3
        ),
        SyllabusModel(
            "Auditing",
            code = "MT310",
            credits = 3,
            openCode = "BBA66",
            type = "PE",
            group = "FINANCE GROUP",
            shortName = "aud",
            listOrder = 4
        ),
        SyllabusModel(
            "Industrial and Labour Legislation",
            code = "MT324",
            credits = 3,
            openCode = "BBA67",
            type = "PE",
            group = "HUMAN RESOURCE GROUP",
            shortName = "ill",
            listOrder = 5
        ),
        SyllabusModel(
            "Industrial Relations",
            code = "MT322",
            credits = 3,
            openCode = "BBA68",
            type = "PE",
            group = "HUMAN RESOURCE GROUP",
            shortName = "ir",
            listOrder = 6
        ),
        SyllabusModel(
            "Performance & Compensation Management",
            code = "MT325",
            credits = 3,
            openCode = "BBA610",
            type = "PE",
            group = "HUMAN RESOURCE GROUP",
            shortName = "pcm",
            listOrder = 7
        ),

        SyllabusModel(
            "Computer Networks",
            code = "MT311",
            credits = 3,
            openCode = "BBA612",
            type = "PE",
            group = "INFORMATION TECHNOLOGY GROUP",
            shortName = "cn",
            listOrder = 8
        ),
        SyllabusModel(
            "Internet and Web Page Design",
            code = "MT313",
            credits = 3,
            openCode = "BBA613",
            type = "PE",
            group = "INFORMATION TECHNOLOGY GROUP",
            shortName = "iwpd",
            listOrder = 9
        ),
        SyllabusModel(
            "Introduction to business analytics",
            code = "MT314",
            credits = 3,
            openCode = "BBA614",
            type = "PE",
            group = "INFORMATION TECHNOLOGY GROUP",
            shortName = "iba",
            listOrder = 10
        ),
        SyllabusModel(
            "Knowledge Management",
            code = "MT312",
            credits = 3,
            openCode = "BBA615",
            type = "PE",
            group = "INFORMATION TECHNOLOGY GROUP",
            shortName = "km",
            listOrder = 11
        ),
        SyllabusModel(
            "Programming Technology",
            code = "MT315",
            credits = 3,
            openCode = "BBA616",
            type = "PE",
            group = "INFORMATION TECHNOLOGY GROUP",
            shortName = "pt",
            listOrder = 12
        ),
        SyllabusModel(
            "Consumer Behaviour",
            code = "MT320",
            credits = 3,
            openCode = "BBA617",
            type = "PE",
            group = "MARKETING GROUP",
            shortName = "cb",
            listOrder = 13
        ),
        SyllabusModel(
            "Integrated Marketing Communication",
            code = "MT319",
            credits = 3,
            openCode = "BBA618",
            type = "PE",
            group = "MARKETING GROUP",
            shortName = "imc",
            listOrder = 14
        ),
    )
}