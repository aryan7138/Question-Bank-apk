package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun UserGuideScreen() {
    val expandedStates = remember {
        mutableStateMapOf<Int, Boolean>().apply {
            put(0, true) // first one open by default
        }
    }

    val guideSections = listOf(
        "অ্যাপ্লিকেশন পরিচিতি (Overview)" to "স্মার্ট প্রশ্নপত্র নির্মাতা (Smart Question Paper Maker) একটি সম্পূর্ণ অফলাইন প্রশ্নপত্র তৈরির অ্যাপ্লিকেশন। আপনার সমস্ত প্রশ্ন ও সংরক্ষিত প্রশ্নপত্র সম্পূর্ণ নিরাপদে আপনার ডিভাইসে সংরক্ষিত থাকে। কোনো ইন্টারনেট কানেকশন বা অ্যাকাউন্টের প্রয়োজন নেই।",
        "প্রশ্ন ব্যাংক তৈরি (Building Question Bank)" to "• একক সংযোজন (Single Entry): বিষয়, শ্রেণি, অধ্যায় এবং ধরন (MCQ, CQ, Short) নির্বাচন করে প্রশ্ন যোগ করুন। MCQ এর ক্ষেত্রে ৪ বা ৫টি অপশন এবং সঠিক উত্তর চিহ্নিত করতে পারেন। CQ এর ক্ষেত্রে উদ্দীপক এবং ক, খ, গ, ঘ উপ-প্রশ্ন ও তাদের মান যোগ করা যায়।\n• বাল্ক পেস্ট (Bulk Paste): যেকোনো সাজানো টেক্সট হুবহু পেস্ট করুন। অ্যাপটি স্বয়ংক্রিয়ভাবে নম্বর ১।, ২। ও অপশন ক) খ) গ) ঘ) সনাক্ত করে আলাদা প্রশ্নে রূপান্তর করবে।",
        "বোর্ড প্রশ্ন পেস্ট (Board Questions Paste)" to "বোর্ড প্রশ্ন ট্যাবে বোর্ড নাম (যেমন: ঢাকা, রাজশাহী), সাল (যেমন: ২০২৪) ও পরীক্ষার ধরন (SSC/HSC) নির্বাচন করে বিগত বছরের প্রশ্ন পেস্ট করুন। প্রতিটি প্রশ্নে স্বয়ংক্রিয়ভাবে বোর্ড ট্যাগ যুক্ত হয়ে যাবে, যা পরবর্তীতে ফিল্টারে সহজে খুঁজে পাওয়া যাবে।",
        "প্রশ্নপত্র তৈরি (Create Paper Wizard)" to "১. সেটআপ: প্রতিষ্ঠানের নাম, পরীক্ষার নাম, সময়, পূর্ণমান ও নির্দেশনা লিখুন।\n২. সেকশন: 'ক বিভাগ - বহুনির্বাচনী', 'খ বিভাগ - সৃজনশীল' ইত্যাদি সেকশন যোগ করুন।\n৩. প্রশ্ন নির্বাচন: প্রশ্ন ব্যাংক থেকে বিষয় ও শ্রেণি অনুযায়ী প্রশ্ন বেছে সেকশনে যোগ করুন।\n৪. প্রিভিউ ও প্রিন্ট: বাস্তব প্রশ্নপত্রের নমুনা দেখুন, পূর্ণমান মিলিয়ে নিন এবং প্রিন্ট বা PDF হিসেবে সংরক্ষণ করুন।",
        "প্রিন্ট ও PDF ডাউনলোড (Print & Export)" to "প্রিভিউ স্ক্রিনে 'PDF ডাউনলোড' চাপলে স্ট্যান্ডার্ড A4 মাপের হাই-কোয়ালিটি PDF তৈরি হয়ে শেয়ার শিট উন্মুক্ত হবে, যা যেকোনো প্রিন্টারে পাঠানো বা ড্রাইভে সেভ করা যাবে। 'TXT টেক্সট' বাটনে ক্লিক করে মাইক্রোসফট ওয়ার্ড বা নোটপ্যাডে এডিটযোগ্য ফরম্যাটে টেক্সট রপ্তানি করা যাবে।",
        "টেম্পলেট ও সংস্করণ (Templates & Versions)" to "একটি প্রশ্নপত্রের লেআউট এবং সেকশন কাঠামো ভবিষ্যতে একই রকম পরীক্ষার জন্য টেম্পলেট হিসেবে সেভ করে রাখতে পারেন। এছাড়াও একই প্রশ্নপত্রের সংশোধিত সংস্করণ (v1, v2) স্বয়ংক্রিয়ভাবে ট্র্যাক করা হয়।"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.MenuBook, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.padding(horizontal = 4.dp))
            Text(
                text = "ব্যবহার নির্দেশিকা (User Guide)",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            itemsIndexed(guideSections) { index, (title, content) ->
                val isExpanded = expandedStates[index] ?: false
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expandedStates[index] = !isExpanded }
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Icon(
                                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary
                            )
                        }

                        AnimatedVisibility(visible = isExpanded) {
                            Column {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = content,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                                    lineHeight = 20.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
