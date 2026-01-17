# 🔑 YouTube API Key Setup - Complete Guide

## 🚀 **Quick Setup (5 Minutes)**

### **Step 1: Google Cloud Console**
1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Sign in with your Google account
3. Click "Create Project" or select existing project
4. Name your project: "FlyMusic AI"

### **Step 2: Enable YouTube Data API**
1. In the left sidebar, click "APIs & Services" → "Library"
2. Search for "YouTube Data API v3"
3. Click on "YouTube Data API v3"
4. Click "ENABLE" button

### **Step 3: Create API Key**
1. Go to "APIs & Services" → "Credentials"
2. Click "CREATE CREDENTIALS" → "API Key"
3. Copy the generated API key
4. Click "RESTRICT KEY" for security

### **Step 4: Configure API Key**
1. Under "API restrictions":
   - Select "Restrict key"
   - Choose "YouTube Data API v3"
2. Under "Website restrictions":
   - Add `localhost:3000`
   - Add `127.0.0.1:3000`
   - Add your production domain
3. Click "SAVE"

## 🔧 **Integration with FlyMusic AI**

### **Method 1: Direct Integration**
1. Open `music-api.js`
2. Find line: `this.youtubeApiKey = 'YOUR_YOUTUBE_API_KEY';`
3. Replace with your actual API key:
```javascript
this.youtubeApiKey = 'AIzaSyBGz_JOm8niyiOwIrW0xcKTLtPBb9ohgUA'; // Your key here
```

### **Method 2: Environment Variables (Recommended)**
1. Create `.env` file in web folder:
```env
YOUTUBE_API_KEY=your_actual_api_key_here
FIREBASE_API_KEY=your_firebase_key_here
```

2. Update `music-api.js`:
```javascript
this.youtubeApiKey = process.env.YOUTUBE_API_KEY || 'YOUR_YOUTUBE_API_KEY';
```

## 📊 **API Quotas & Limits**

### **Free Tier Limits**
- **Daily Quota**: 10,000 units
- **Search Request**: 100 units each
- **Video Details**: 1 unit each
- **Rate Limit**: 100 requests per 100 seconds per user

### **Cost Optimization Tips**
1. **Cache Results**: Store popular searches
2. **Batch Requests**: Get multiple videos at once
3. **Smart Pagination**: Load more only when needed
4. **Fallback Data**: Use sample data when quota exceeded

## 🧪 **Testing Your API Key**

### **Test API Call**
```javascript
// Test in browser console
const testYouTubeAPI = async () => {
    const apiKey = 'YOUR_API_KEY';
    const query = 'Arijit Singh';
    const url = `https://www.googleapis.com/youtube/v3/search?part=snippet&type=video&videoCategoryId=10&q=${query}&maxResults=5&key=${apiKey}`;
    
    try {
        const response = await fetch(url);
        const data = await response.json();
        console.log('API Test Success:', data);
        return data.items;
    } catch (error) {
        console.error('API Test Failed:', error);
    }
};

// Run test
testYouTubeAPI();
```

### **Expected Response**
```json
{
  "items": [
    {
      "id": {
        "videoId": "abc123"
      },
      "snippet": {
        "title": "Song Title",
        "channelTitle": "Artist Name",
        "thumbnails": {
          "medium": {
            "url": "https://..."
          }
        }
      }
    }
  ]
}
```

## 🔒 **Security Best Practices**

### **API Key Security**
1. **Never commit** API keys to version control
2. **Use environment variables** in production
3. **Restrict by domain** in Google Cloud Console
4. **Monitor usage** regularly
5. **Rotate keys** periodically

### **Domain Restrictions**
```
Development:
- localhost:3000
- 127.0.0.1:3000

Production:
- yourdomain.com
- www.yourdomain.com
- your-app.netlify.app
```

## 📈 **Monitoring & Analytics**

### **Google Cloud Console Monitoring**
1. Go to "APIs & Services" → "Dashboard"
2. Click on "YouTube Data API v3"
3. Monitor:
   - **Requests per day**
   - **Quota usage**
   - **Error rates**
   - **Traffic by country**

### **Set Up Alerts**
1. Go to "Monitoring" → "Alerting"
2. Create alert for:
   - **90% quota usage**
   - **High error rates**
   - **Unusual traffic patterns**

## 🚨 **Troubleshooting**

### **Common Errors**

#### **403 Forbidden**
```json
{
  "error": {
    "code": 403,
    "message": "The request cannot be completed because you have exceeded your quota."
  }
}
```
**Solution**: Wait for quota reset or upgrade plan

#### **400 Bad Request**
```json
{
  "error": {
    "code": 400,
    "message": "API key not valid."
  }
}
```
**Solution**: Check API key and restrictions

#### **CORS Error**
```
Access to fetch at 'https://www.googleapis.com/youtube/v3/...' from origin 'http://localhost:3000' has been blocked by CORS policy
```
**Solution**: Use HTTPS or implement server-side proxy

### **Debug Checklist**
- [ ] API key is correct
- [ ] YouTube Data API v3 is enabled
- [ ] Domain restrictions are set properly
- [ ] Quota is not exceeded
- [ ] Request format is correct

## 🎵 **Real Music Integration**

### **After API Setup**
1. **Test with real searches**: "Arijit Singh", "Bollywood hits"
2. **Verify video playback**: Check if videos are playable
3. **Monitor performance**: Ensure fast loading
4. **Handle errors**: Graceful fallbacks to sample data

### **Enhanced Features with Real API**
- **Live search results** from YouTube
- **Trending music** updates automatically  
- **Related videos** suggestions
- **Real-time popularity** data
- **Music video thumbnails** and metadata

## 💰 **Scaling & Costs**

### **Free Tier Strategy**
- **10,000 units/day** = ~100 searches
- **Cache popular results** to reduce API calls
- **Use sample data** as fallback
- **Implement smart pagination**

### **Paid Tier Benefits**
- **Higher quotas** for more users
- **Better performance** guarantees
- **Priority support** from Google
- **Advanced analytics** and monitoring

---

## 🎯 **Quick Action Items**

1. **Get API Key** (5 minutes)
2. **Test API** (2 minutes)  
3. **Update Code** (1 minute)
4. **Deploy & Test** (5 minutes)

**Your FlyMusic AI will have real YouTube music streaming in under 15 minutes!** 🚀🎵
