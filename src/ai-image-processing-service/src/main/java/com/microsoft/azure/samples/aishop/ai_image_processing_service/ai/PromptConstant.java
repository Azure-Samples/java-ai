package com.microsoft.azure.samples.aishop.ai_image_processing_service.ai;

public class PromptConstant {
    public static String ITEM_DESCRIPTION_SYSTEM_PROMPT = """
        You are an expert in the field of e-commerce and you have been asked to provide a description of an item that is available for sale. You act as a professional that is at the service of the customer to describe objectively the item that is available for sale.""";
    
    public static String ITEM_DESCRIPTION_USER_PROMPT = """
    Describe the item for sale that is represented on the picture:
        - Give a short descriptive label for item. The label should describe well the item so a potential buyer can understand what is being sold.
        - Provide a detailed description of the item. Include all relevant information that a potential buyer would need to know before making a purchase.
        - Specify the condition of the item. The condition should be one of the following: New, Like New, Used, Refurbished.
        - Provide the price of the item in Dollar currency. The price should be in USD and should be a number with two decimal places. For example, 10.00.
        - Provide the brand. If the brand is unknown, use the word "Unknown".
        - Provide the model. If the model is unknown, use the word "Unknown".
    
    In the label, if you can, include the brand, model, and any other relevant information that would help a potential buyer understand what is being sold.
    
    In the description, include the following:
      - The brand of the item if applicable.
      - The model of the item if applicable.
      - The size of the item if applicable.
      - A detailed description of the item.
      - A description of the condition of the item.
      - Any other relevant information that would help a potential buyer understand what is being sold.
    
    
    ### START Example of Title
    Title: Apple iPhone 12 Pro Max 256GB Pacific Blue
    ### END Example of Title
    
    ### START Example of Description
    Brand: Apple
    Model: iPhone 12 Pro Max
    
    Description:
    This is a brand new Apple iPhone 12 Pro Max in the color Pacific Blue. The phone has 256GB of storage. The phone comes with the original box and all accessories. The phone has a 6.7-inch Super Retina XDR display and is powered by the A14 Bionic chip. The phone has a triple-camera system with 12MP Ultra Wide, Wide, and Telephoto cameras. The phone has 5G capabilities and is water and dust resistant. The phone has Face ID for secure authentication.
    
    Condition: New
    The phone is in perfect condition and has never been used. 
    
    Other Information:
    The phone is perfect for anyone looking for a high-end smartphone with all the latest features.
    ### END Example of Description
    
    Return all the information in a JSON format with the following structure:
    {
        "label": "Item label",
        "brand": "Item brand",
        "model": "Item model",
        "condition": "Item condition",
        "price": 0.00,
        "description": "Item description"
    }
    """;
    
    private PromptConstant() {
    }
}
