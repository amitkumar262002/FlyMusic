package com.example.flymusicai.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flymusicai.ui.theme.*

// Reusing existing data model name but adapting structure if needed,
// or defining new ones specific to this screen to avoid conflicts if used elsewhere.
data class ProPlanData(
        val id: String,
        val title: String,
        val price: String,
        val originalPrice: String? = null,
        val duration: String,
        val discountTag: String? = null,
        val badge: String? = null,
        val benefits: List<String>,
        val users: String, // e.g., "1 User", "6 Users"
        val gradientColors: List<Color>,
        val buttonText: String,
        val buttonColor: Color,
        val bottomNote: String? = null,
        val titleColor: Color = Color.White
)

data class FAQItemData(val question: String, val answer: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProScreen(modifier: Modifier = Modifier) {
        val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

        // Plan Definitions based on screenshots
        val plans = remember {
                listOf(
                        ProPlanData(
                                id = "individual",
                                title = "Individual",
                                price = "₹0",
                                duration = "For 1 month",
                                discountTag = "100% off",
                                badge = "BEST SELLER \uD83D\uDD25",
                                benefits = listOf("All the Pro benefits"),
                                users = "1 User",
                                gradientColors =
                                        listOf(
                                                Color(0xFF1DB954).copy(alpha = 0.3f),
                                                Color.Black
                                        ), // Greenish
                                buttonText = "Get Pro Individual",
                                buttonColor = Color(0xFFBCF563), // Light Lime Green
                                bottomNote = "Renews in 1 Month",
                                titleColor = Color(0xFFBCF563)
                        ),
                        ProPlanData(
                                id = "duo",
                                title = "Duo",
                                price = "₹129",
                                originalPrice = "₹298",
                                duration = "For 2 months",
                                discountTag = "55% off",
                                badge = "₹129 for 2 months",
                                benefits = listOf("All the Pro benefits"),
                                users = "2 Users",
                                gradientColors =
                                        listOf(
                                                AmberGold.copy(alpha = 0.3f),
                                                Color.Black
                                        ), // Goldish
                                buttonText = "Get Pro Duo",
                                buttonColor = AmberGold, // Yellow
                                bottomNote = "*Save 13% with autopay",
                                titleColor = AmberGold
                        ),
                        ProPlanData(
                                id = "family",
                                title = "Family",
                                price = "₹149",
                                originalPrice = "₹358",
                                duration = "For 2 months",
                                discountTag = "55% off",
                                badge = "₹149 for 2 months",
                                benefits = listOf("All the Pro benefits"),
                                users = "6 Users",
                                gradientColors =
                                        listOf(
                                                Color(0xFFFF9F43).copy(alpha = 0.3f),
                                                Color.Black
                                        ), // Orange
                                buttonText = "Get Pro Family",
                                buttonColor = Color(0xFFFF9F43), // Orange
                                bottomNote = "*Save 17% with autopay",
                                titleColor = Color(0xFFFF9F43)
                        ),
                        ProPlanData(
                                id = "student",
                                title = "Student",
                                price = "₹49",
                                originalPrice = "₹99",
                                duration = "For 1 month",
                                benefits = listOf("All the Pro benefits"),
                                users = "1 User",
                                gradientColors =
                                        listOf(
                                                Color(0xFF00D2D3).copy(alpha = 0.3f),
                                                Color.Black
                                        ), // Teal
                                buttonText = "Get Pro Student",
                                buttonColor = Color(0xFF54EAD0), // Light Teal
                                bottomNote = "Save up to 67%; only for students",
                                titleColor = Color(0xFF54EAD0)
                        ),
                        ProPlanData(
                                id = "flytunes",
                                title = "FlyTunes+",
                                price = "₹9",
                                originalPrice = "₹59",
                                duration = "For 1 month",
                                discountTag = "85% off",
                                benefits = listOf("Unlimited FlyTunes from 10 Lakh+ songs"),
                                users = "", // No specific user count mentioned usually for this, or
                                // imply 1
                                gradientColors =
                                        listOf(
                                                Color(0xFFD4E157).copy(alpha = 0.3f),
                                                Color.Black
                                        ), // Lime/Yellow
                                buttonText = "Get FlyTunes+",
                                buttonColor = Color(0xFFD4E157), // Lime Yellow
                                bottomNote = "*Save 17% with autopay",
                                titleColor = Color(0xFFD4E157)
                        ),
                        ProPlanData(
                                id = "lite",
                                title = "Lite",
                                price = "₹9",
                                duration = "For 1 day",
                                badge = "₹9 for 1 day",
                                benefits = listOf("Ad-free Music", "Unlimited Downloads"),
                                users = "1 User",
                                gradientColors =
                                        listOf(
                                                Color(0xFF2E86DE).copy(alpha = 0.3f),
                                                Color.Black
                                        ), // Blue
                                buttonText = "Get Pro Lite",
                                buttonColor = Color(0xFF48DBFB), // Light Blue
                                bottomNote = "Unlimited FlyTunes not available with Pro Lite",
                                titleColor = Color(0xFF48DBFB)
                        )
                )
        }

        val faqs = remember {
                listOf(
                        FAQItemData(
                                "What is FlyMusic Pro?",
                                "FlyMusic Pro is our premium subscription service that offers ad-free music, high-quality audio, unlimited downloads, and more."
                        ),
                        FAQItemData(
                                "How do I purchase or switch my plan?",
                                "You can tap on any plan above to purchase. To switch, simply select a new plan and follow the instructions."
                        ),
                        FAQItemData(
                                "Why is my subscription missing?",
                                "Try restoring your purchase from the settings menu or contact support if the issue persists."
                        ),
                        FAQItemData(
                                "What should I do if my payment fails?",
                                "Check your internet connection and payment details. If the problem continues, try a different payment method."
                        )
                )
        }

        Scaffold(
                modifier = modifier.fillMaxSize(),
                topBar = {
                        LargeTopAppBar(
                                title = {
                                        Text(
                                                "Choose Your Pack",
                                                fontSize = 24.sp,
                                                fontWeight = FontWeight.Bold
                                        )
                                },
                                colors =
                                        TopAppBarDefaults.largeTopAppBarColors(
                                                containerColor = Color.Black,
                                                titleContentColor = Color.White,
                                                scrolledContainerColor = Color.Black
                                        ),
                                scrollBehavior = scrollBehavior
                        )
                },
                containerColor = Color.Black
        ) { innerPadding ->
                LazyColumn(
                        modifier =
                                Modifier.fillMaxSize()
                                        .padding(innerPadding)
                                        .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                        item { Spacer(modifier = Modifier.height(8.dp)) }

                        items(plans) { plan ->
                                NewPlanCard(plan = plan, onClick = { /* Handle Plan Selection */})
                        }

                        item { NewRedeemCouponCard() }

                        item {
                                Text(
                                        text = "FAQs",
                                        style = MaterialTheme.typography.titleLarge,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(vertical = 16.dp)
                                )
                        }

                        items(faqs) { faq -> NewFAQItemRow(faq) }

                        item {
                                Spacer(
                                        modifier = Modifier.height(100.dp)
                                ) // Bottom padding for player
                        }
                }
        }
}

@Composable
fun NewPlanCard(plan: ProPlanData, onClick: () -> Unit) {
        Card(
                modifier = Modifier.fillMaxWidth().wrapContentHeight().clickable { onClick() },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF121212)),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
                Column(
                        modifier =
                                Modifier.background(
                                        brush = Brush.verticalGradient(colors = plan.gradientColors)
                                )
                ) {
                        // Header Row (Pro Badge & Special Badge)
                        Row(
                                modifier =
                                        Modifier.fillMaxWidth()
                                                .padding(top = 16.dp, start = 16.dp, end = 0.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                                imageVector =
                                                        Icons.Default
                                                                .LocalOffer, // Placeholder icon
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                                text = "PRO",
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp
                                        )
                                }

                                if (plan.badge != null) {
                                        Box(
                                                modifier =
                                                        Modifier.background(
                                                                        color =
                                                                                if (plan.badge
                                                                                                .contains(
                                                                                                        "BEST"
                                                                                                )
                                                                                )
                                                                                        Color(
                                                                                                0xFFBCF563
                                                                                        )
                                                                                else if (plan.badge
                                                                                                .contains(
                                                                                                        "Student"
                                                                                                )
                                                                                )
                                                                                        Color(
                                                                                                0xFF54EAD0
                                                                                        )
                                                                                else if (plan.badge
                                                                                                .contains(
                                                                                                        "day"
                                                                                                )
                                                                                )
                                                                                        Color(
                                                                                                0xFF48DBFB
                                                                                        )
                                                                                else
                                                                                        plan.buttonColor
                                                                                                .copy(
                                                                                                        alpha =
                                                                                                                0.8f
                                                                                                ),
                                                                        shape =
                                                                                RoundedCornerShape(
                                                                                        topStart =
                                                                                                4.dp,
                                                                                        bottomStart =
                                                                                                4.dp
                                                                                )
                                                                )
                                                                .padding(
                                                                        horizontal = 8.dp,
                                                                        vertical = 4.dp
                                                                )
                                        ) {
                                                Text(
                                                        text = plan.badge,
                                                        color = Color.Black,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 12.sp
                                                )
                                        }
                                }
                        }

                        // Title
                        Text(
                                text = plan.title,
                                style =
                                        MaterialTheme.typography.headlineMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = plan.titleColor
                                        ),
                                modifier = Modifier.padding(start = 16.dp, top = 8.dp)
                        )

                        // Price Row
                        Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                                Text(
                                        text = plan.price,
                                        style =
                                                MaterialTheme.typography.titleLarge.copy(
                                                        fontWeight = FontWeight.Bold
                                                ),
                                        color = Color.White
                                )

                                if (plan.originalPrice != null) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                                text = plan.originalPrice,
                                                style =
                                                        MaterialTheme.typography.bodyLarge.copy(
                                                                textDecoration =
                                                                        TextDecoration.LineThrough
                                                        ),
                                                color = Color.Gray
                                        )
                                }

                                if (plan.discountTag != null) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Box(
                                                modifier =
                                                        Modifier.background(
                                                                        Color(0xFF333333),
                                                                        RoundedCornerShape(4.dp)
                                                                )
                                                                .padding(
                                                                        horizontal = 6.dp,
                                                                        vertical = 2.dp
                                                                )
                                        ) {
                                                Text(
                                                        text = plan.discountTag,
                                                        color = Color(0xFFFF9F43),
                                                        style = MaterialTheme.typography.labelMedium
                                                )
                                        }
                                }
                        }

                        Text(
                                text = plan.duration,
                                color = Color.White.copy(alpha = 0.9f),
                                modifier = Modifier.padding(start = 16.dp, bottom = 12.dp),
                                fontSize = 14.sp
                        )

                        HorizontalDivider(
                                color = Color.White.copy(alpha = 0.1f),
                                modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        // Benefits
                        Column(modifier = Modifier.padding(16.dp)) {
                                plan.benefits.forEach { benefit ->
                                        Row(
                                                modifier = Modifier.padding(vertical = 4.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                        ) {
                                                Icon(
                                                        imageVector = Icons.Default.Check,
                                                        contentDescription = null,
                                                        tint = Color.White,
                                                        modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                        text = benefit,
                                                        color = Color.White.copy(alpha = 0.9f),
                                                        fontSize = 14.sp
                                                )
                                        }
                                }
                                Row(
                                        modifier = Modifier.padding(vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                ) {
                                        Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                                text = plan.users,
                                                color = Color.White.copy(alpha = 0.9f),
                                                fontSize = 14.sp
                                        )
                                }
                        }

                        // Button
                        Button(
                                onClick = onClick,
                                modifier =
                                        Modifier.fillMaxWidth()
                                                .padding(horizontal = 16.dp, vertical = 12.dp)
                                                .height(48.dp),
                                colors =
                                        ButtonDefaults.buttonColors(
                                                containerColor = plan.buttonColor
                                        ),
                                shape = RoundedCornerShape(8.dp)
                        ) {
                                Text(
                                        text = plan.buttonText,
                                        color = Color.Black,
                                        fontWeight = FontWeight.Bold
                                )
                        }

                        // Bottom Note
                        if (plan.bottomNote != null) {
                                Box(
                                        modifier =
                                                Modifier.fillMaxWidth()
                                                        .background(Color.White.copy(alpha = 0.1f))
                                                        .padding(vertical = 12.dp),
                                        contentAlignment = Alignment.Center
                                ) {
                                        Text(
                                                text = plan.bottomNote,
                                                color = Color.White.copy(alpha = 0.8f),
                                                fontSize = 12.sp
                                        )
                                }
                        }
                }
        }
}

@Composable
fun NewRedeemCouponCard() {
        Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                colors =
                        CardDefaults.cardColors(
                                containerColor = Color(0xFF1E1E1E)
                        ), // Dark Brownish/Gray
                shape = RoundedCornerShape(12.dp)
        ) {
                Row(
                        modifier = Modifier.fillMaxWidth().padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                ) {
                        Column(modifier = Modifier.weight(1f)) {
                                Text(
                                        text = "Redeem Your FlyMusic Pro",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                )
                                Text(
                                        text = "Coupon code",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                OutlinedButton(
                                        onClick = { /* Handle Redeem */},
                                        colors =
                                                ButtonDefaults.outlinedButtonColors(
                                                        contentColor = Color(0xFF00D2D3)
                                                ),
                                        border = BorderStroke(1.dp, Color(0xFF00D2D3)),
                                        shape = RoundedCornerShape(18.dp)
                                ) { Text("Redeem Now") }
                        }

                        // Coupon Icon Placeholder
                        Box(
                                modifier =
                                        Modifier.size(60.dp)
                                                .background(
                                                        Color(0xFF332E27),
                                                        RoundedCornerShape(12.dp)
                                                ), // Brownish
                                contentAlignment = Alignment.Center
                        ) {
                                Icon(
                                        imageVector = Icons.Default.LocalOffer,
                                        contentDescription = null,
                                        tint = Color(0xFFFF9F43),
                                        modifier = Modifier.size(32.dp)
                                )
                        }
                }
        }
}

@Composable
fun NewFAQItemRow(item: FAQItemData) {
        var expanded by remember { mutableStateOf(false) }
        val rotationState by
                animateFloatAsState(targetValue = if (expanded) 180f else 0f, label = "rotation")

        Column(modifier = Modifier.fillMaxWidth().clickable { expanded = !expanded }) {
                Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                ) {
                        Text(
                                text = item.question,
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.White,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.weight(1f)
                        )
                        Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.rotate(rotationState)
                        )
                }

                AnimatedVisibility(visible = expanded) {
                        Text(
                                text = item.answer,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray,
                                modifier = Modifier.padding(bottom = 16.dp)
                        )
                }
                HorizontalDivider(color = Color.Gray.copy(alpha = 0.2f))
        }
}
